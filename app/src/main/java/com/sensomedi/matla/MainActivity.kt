package com.sensomedi.matla

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sensomedi.data.*
import com.sensomedi.matla.ble.*
import com.sensomedi.matla.databinding.ActivityMainBinding
import com.sensomedi.matla.fragment.DataFragment
import com.sensomedi.matla.fragment.MatlaFragment
import com.sensomedi.matla.fragment.SettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var device: BluetoothDevice? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var db: MatlaDatabase
    private lateinit var auth: FirebaseAuth

    var measureData = mutableListOf<Temporary>()

    var uid = ""

    private val firebaseUser = Firebase.database.reference.child("users").child(uid)
    var matlas = mutableListOf<MatlaData>()

    var time = 7200000L
    val measureTime = object : CountDownTimer(time, 1000 * 60) {
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = MatlaDatabase.getInstance(applicationContext)!!
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        uid = intent.getStringExtra("uid") ?: ""
        val bool = intent.getBooleanExtra("bool", false)

        if (!bool) {
            Firebase.database.reference.child("users").child(auth.uid!!).get()
                .addOnSuccessListener {
                    val user = User(
                        it.child("email").value.toString(),
                        it.child("age").value.toString().toInt(),
                        it.child("gender").value.toString(),
                        it.child("height").value.toString().toInt(),
                        it.child("weight").value.toString().toInt(),
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        db.userDao().insertUser(
                            User(
                                user.email,
                                user.age,
                                user.gender,
                                user.height,
                                user.weight
                            )
                        )
                    }
                    viewModel.setUser(user)
                }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.setUser(db.userDao().getUser()[0])
                println(db.userDao().getUser())
            }
        }

        viewModel.measureTime.observe(this) {
            time = it
        }
        requestPermission()
        replaceFragment(MatlaFragment())
        initView()

    }

    private fun startTimer() {
        val countDown = object : CountDownTimer(viewModel.measureTime.value!!, 1000 * 60 * 59) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                disconnect()
                save()
            }
        }
        countDown.start()
    }

    fun saveData(height: Int, weight: Int, age: Int) {
        Firebase.database.reference.child("users").child(uid).child("age").setValue(age)
        Firebase.database.reference.child("users").child(uid).child("height").setValue(height)
        Firebase.database.reference.child("users").child(uid).child("weight").setValue(weight)
        val user =
            viewModel.user.value?.let {
                User(
                    it.email,
                    age,
                    it.gender,
                    height,
                    weight,
                )
            }
        if (user != null) {
            viewModel.setUser(user)
        }
        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().deleteUser()
            if (user != null) {
                db.userDao().insertUser(user)
            }
        }
    }

    fun withdraw() {
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener {
            if (it.isSuccessful) {
                Firebase.database.reference.child("users").child(uid).removeValue()
                Toast.makeText(this, "Withdrawal Complete", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    db.userDao().deleteUser()
                }
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                println(it.exception.toString())
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFirebaseData() {
        var matla = arrayListOf<Detail>()
        Firebase.database.reference.child("users").child(uid).child("matla").get()
            .addOnSuccessListener {
                println(it.value)
                println(it.children)
                println(it.ref.toString() + "@@@")
                println(it.children.toList().size)
                if (it.children.toList().isEmpty()) {
                    viewModel.setMatlaListSize(0)
                }
                val k = arrayListOf<Any>()
                for (i in it.children.toList()) {
//                    println(i.child("data").value.toString() + "@@@")
                    val j = i.child("data").value
                    val gson = Gson()
                    val type: Type = object : TypeToken<List<MatlaData?>?>() {}.type
                    val matlaList: List<MatlaData> =
                        gson.fromJson(i.child("data").value.toString(), type)
                    Detail(matlaList)
                    matla.add(Detail(matlaList))
//                    println("@@@$matlaList")
                    k.add(j!!)

                    viewModel.setMatlaDataList(matla)
//                    println("@@@$matla")
                }
            }
    }

    fun getMatlaData() {
        if (viewModel.matlaDataList.value!!.isEmpty()) {
            getFirebaseData()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                requestPermissions(PERMISSIONS_S_ABOVE, REQUEST_ALL_PERMISSION)
            }
        } else {
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
            }
        }
    }

    private fun initView() = with(binding) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_matla -> {
                    replaceFragment(MatlaFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.item_setting -> {
                    replaceFragment(SettingFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.item_data -> {
                    replaceFragment(DataFragment())
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener true
            }
        }
    }


    fun storeData(matla: MatlaData) {
        CoroutineScope(Dispatchers.IO).launch {
            db.dataDao().insert(matla)
        }
    }

    fun getData() {
        CoroutineScope(Dispatchers.IO).launch {
            println(db.dataDao().getAll().toString() + "@@@@")
        }
    }

    private val characteristics by lazy {
        ConnectionManager.servicesOnDevice(device!!)?.flatMap { service ->
            service.characteristics ?: listOf()
        } ?: listOf()
    }

    private val characteristicProperties by lazy {
        characteristics.map { characteristic ->
            characteristic to mutableListOf<CharacteristicProperty>().apply {
                if (characteristic.isNotifiable()) add(CharacteristicProperty.Notifiable)
                if (characteristic.isIndicatable()) add(CharacteristicProperty.Indicatable)
                if (characteristic.isReadable()) add(CharacteristicProperty.Readable)
                if (characteristic.isWritable()) add(CharacteristicProperty.Writable)
                if (characteristic.isWritableWithoutResponse()) {
                    add(CharacteristicProperty.WritableWithoutResponse)
                }
            }.toList()
        }.toMap()
    }


    private var notifyingCharacteristics = mutableListOf<UUID>()
    private val characteristicAdapter: CharacteristicAdapter by lazy {
        CharacteristicAdapter(characteristics) { characteristic ->
            showCharacteristicOptions(characteristic)
        }
    }

    fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        device?.let {
            ConnectionManager.teardownConnection(it)
            ConnectionManager.unregisterListener(connectionEventListener)
        }
        super.onDestroy()
    }

    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopBleScan() {
        CoroutineScope(Dispatchers.Main).launch {
            bleScanner.stopScan(scanCallback)
            delay(1000L)
            viewModel.setScanning(false)
        }
    }

    fun finishMeasure() {
        CoroutineScope(Dispatchers.IO).launch {
//            db.dataDao().insert()
        }
    }

    fun disconnect() {
        println("@@TEAR")
        ConnectionManager.teardownConnection(device!!)
        viewModel.setMeasure(false)
    }

    @SuppressLint("MissingPermission")
    fun startBleScan() {
        scanResults.clear()
        bleScanner.startScan(null, scanSettings, scanCallback)
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.isScanning.value!!) {
                stopBleScan()
            }
        }, 3000) //1초 후 실행

        viewModel.setScanning(true)
    }

    private val scanResults = mutableListOf<ScanResult>()

    val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    fun startMeasure() {
        measureData = mutableListOf()
        startTimer()
        for (i in characteristics) {
            showCharacteristicOptions(i)
        }
    }

    fun showCharacteristicOptions(characteristic: BluetoothGattCharacteristic) {
        characteristicProperties[characteristic]?.let { properties ->
            for (i in properties) {
                when (i) {
                    CharacteristicProperty.Readable -> {
                        println("Reading from ${characteristic.uuid}")
                        ConnectionManager.readCharacteristic(device!!, characteristic)
                    }
                    CharacteristicProperty.Notifiable, CharacteristicProperty.Indicatable -> {
                        ConnectionManager.enableNotifications(device!!, characteristic)
//                        if (notifyingCharacteristics.contains(characteristic.uuid)) {
//                            println("Disabling notifications on ${characteristic.uuid}")
//                            ConnectionManager.disableNotifications(device, characteristic)
//                        } else {
//                            println("Enabling notifications on ${characteristic.uuid}")
//                            ConnectionManager.enableNotifications(device, characteristic)
//                        }
                    }
                    else -> {}
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
            } else {
                with(result.device) {
                    println("Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)
            }
            if (scanResults.isNotEmpty()) {
                for (i in scanResults) {
                    if (i.device.name == "SemiElec" || i.device.name == "SENSOMEDI") {
                        device = i.device
                        ConnectionManager.connect(i.device, this@MainActivity)
                        stopBleScan()
                        Toast.makeText(this@MainActivity, "Success Connection.", Toast.LENGTH_SHORT)
                            .show()
                        viewModel.setConnected(true)
//                        for (j in characteristics) {
//                            showCharacteristicOptions(j)
//                        }
//                        println(characteristics.toString())
//                        for (i in characteristics) {
//                            showCharacteristicOptions(i)
//                        }
                    }
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            println("onScanFailed: code $errorCode")
        }
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onDisconnect = {
                viewModel.setConnected(false)
                viewModel.setMeasure(false)
                println("Disconnecting")
            }

            onCharacteristicRead = { _, characteristic ->
                println("Read from ${characteristic.uuid}: ${characteristic.value.toHexString()}")
            }

            onCharacteristicWrite = { _, characteristic ->
                println("Wrote to ${characteristic.uuid}")
            }

            onMtuChanged = { _, mtu ->
                println("MTU updated to $mtu")
            }

            onCharacteristicChanged = { _, characteristic ->
//                println("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
                if (characteristic.value.toHexString().length > 20) {
                    val array = characteristic.value.toHexString().split(" ")
                    val real = array.subList(4, 19)
                    val sensorData = mutableListOf<Int>()
                    for (i in real) {
                        sensorData.add(i.toLong(16).toInt())
                    }
                    println("$sensorData")
//                    showDataMatla(sensorData)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.setMatlaData(Temporary(System.currentTimeMillis(), sensorData))
                    }
                    measureData.add(Temporary(System.currentTimeMillis(), sensorData))
                }
            }

            onNotificationsEnabled = { _, characteristic ->
                println("Enabled notifications on ${characteristic.uuid}")
            }

            onNotificationsDisabled = { _, characteristic ->
                println("Disabled notifications on ${characteristic.uuid}")
            }
        }
    }

    fun save() {
        Toast.makeText(this, measureData.toString(), Toast.LENGTH_SHORT).show()
        Firebase.database.reference.child("users").child(uid).child("matla").child(
            System.currentTimeMillis()
                .toString()
        ).child("data").setValue(
            measureData
        ).addOnSuccessListener {
            getFirebaseData()
        }
    }


    private enum class CharacteristicProperty {
        Readable,
        Writable,
        WritableWithoutResponse,
        Notifiable,
        Indicatable;

        val action
            get() = when (this) {
                Readable -> "Read"
                Writable -> "Write"
                WritableWithoutResponse -> "Write Without Response"
                Notifiable -> "Toggle Notifications"
                Indicatable -> "Toggle Indications"
            }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.menuFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        ConnectionManager.registerListener(connectionEventListener)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(this, PERMISSIONS_S_ABOVE)) {
                if (!bluetoothAdapter.isEnabled) {
                    promptEnableBluetooth()
                }
            }
        } else {
            if (!hasPermissions(this, PERMISSIONS)) {
                if (!bluetoothAdapter.isEnabled) {
                    promptEnableBluetooth()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val PERMISSIONS = arrayOf(
        ACCESS_FINE_LOCATION
    )
    val PERMISSIONS_S_ABOVE = arrayOf(
        BLUETOOTH_SCAN,
        BLUETOOTH_CONNECT,
        ACCESS_FINE_LOCATION
    )
    val REQUEST_ALL_PERMISSION = 2

}