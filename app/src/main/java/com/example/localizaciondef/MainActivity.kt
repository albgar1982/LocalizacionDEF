package com.example.localizaciondef

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.localizaciondef.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),LocationListener {
    private lateinit var binding : ActivityMainBinding

    val TIEMPO_MIN = (10*1000).toLong()
    val DISTANCIA_MIN = 5.0F
    val A = arrayOf("n/d","preciso","impreciso")
    val P = arrayOf("n/d","bajo","medio","alto")
    val E = arrayOf("fuera de servicio","temporalmente no disponible","disponible")
    lateinit var manejadorLoc:LocationManager
    lateinit var proveedor:String
    lateinit var salida:TextView

    //@SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        salida = binding.tvSalida

        manejadorLoc = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        /*
        val proveedorGPS = LocationManager.GPS_PROVIDER
        println(proveedorGPS)*/

        muestraProveedores()
        val criterio = Criteria().apply {
            isCostAllowed = false
            powerRequirement=Criteria.POWER_LOW
            //isAltitudeRequired=false
            //accuracy = Criteria.ACCURACY_FINE
        }
        proveedor = manejadorLoc.getBestProvider(criterio, true).toString()
        log("Mejor proveedor: $proveedor\n")

        log("Comenzamos con la última localización conocida:")

        muestraLocaliz(manejadorLoc.getLastKnownLocation(proveedor)) //getLastKnownLocation() devuelve un Location, que es el parámetro que espera muestraLocaliz()
    }

    override fun onResume() {
        super.onResume()
        manejadorLoc.requestLocationUpdates(proveedor,TIEMPO_MIN,DISTANCIA_MIN,this)
    }
    override fun onPause() {
        super.onPause()
        manejadorLoc.removeUpdates(this)
    }
    override fun onLocationChanged(location: Location) {
        log("Nueva localización: ")
        muestraLocaliz(location)
    }
    override fun onProviderDisabled(proveedor: String) {
        log("Proveedor deshabilitado: $proveedor\n")
    }
    override fun onProviderEnabled(proveedor: String) {
        log("Proveedor habilitado: $proveedor\n")
    }
    override fun onStatusChanged(proveedor: String,estado:Int,extras:Bundle){
        log("Cambia estado proveedor: $proveedor, estado="+ "${E[Math.max(0,estado)]}, extras= $extras\n")
    }
    @SuppressLint("SetTextI18n")
    private fun log(cadena:String){
        salida.text ="${salida.text} $cadena\n"
    }
    private fun muestraLocaliz(localizacion:Location?){
        if(localizacion == null)
            log("Localizacion desconocida\n")
        else
            log(localizacion.toString() + "\n")
    }
    private fun muestraProveedores(){
        log("Proveedores de localización: \n")
        val proveedores = manejadorLoc.allProviders
        proveedores.forEach {
            muestraProveedor(it)
        }
    }

    private fun muestraProveedor(proveedor: String){
        val proveedorActual= manejadorLoc.getProvider(proveedor)

        proveedorActual?.let {
            log("Proveedor de Localización:\n"+
                    "getName = ${it.name}\n" +
                    "isProviderEnabled=${manejadorLoc.isProviderEnabled(proveedor)}\n"+
                    "getAccuracy=${A[Math.max(0,it.accuracy)]}\n" +
                    "getPowerRequirement=${P[Math.max(0,it.powerRequirement)]}\n" +
                    "hasMonetaryCost=${it.hasMonetaryCost()}\n" +
                    "requiresCell= ${it.requiresCell()}\n" +
                    "requiresNetwrequiresSatellite=${it.requiresSatellite()}\n" +
                    "supportsAltitudork=${it.requiresNetwork()}\\n\" +\n" +
                    "                    \"e=${it.supportsAltitude()}\n" +
                    "supportsBearing=${it.supportsBearing()}\n" +
                    "supportsSpeed=${it.supportsSpeed()}, ]\n\n")
        }
    }
    /*
    Hemos usado métodos deprecated a partir de la API 31. Como no tengo más que Android 10(Q), uso el API 21
    Si se pudiera usar el API 31, cambiar a:

    private fun muestraProveedor(proveedor: String){
        val propiedadesProveedor= manejadorLoc.getProviderProperties(proveedor)
        propiedadesProveedor?.let {
            log("LocationProvider["+"getName = ${it.describeContents()}," +
                    ", isProviderEnabled=${manejadorLoc.isProviderEnabled(proveedor)}, "+
                    "getAccuracy=${A[Math.max(0,it.accuracy)]}," +
                    "getPowerRequirement=${P[Math.max(0,it.powerUsage)]}, " +
                    "hasMonetaryCost=${it.hasMonetaryCost()}, " +
                    "requiresCell= ${it.hasCellRequirement()}, " +
                    "requiresNetwork=${it.hasNetworkRequirement()}, " +
                    "requiresSatellite=${it.hasSatelliteRequirement()}, " +
                    "supportsAltitude=${it.hasAltitudeSupport()}, " +
                    "supportsBearing=${it.hasBearingSupport()}, " +
                    "supportsSpeed=${it.hasSpeedSupport()}, ]\n")
        }
    }

     */

    /*
        val locat= manejadorLoc.getLastKnownLocation(proveedor)

        val coord1= locat?.let {
            listOf(it.altitude,it.longitude,it.latitude)
        }
        val listaCoor = listOf(coord1,listOf(25.3,58.0,48.0),listOf(25.3,58.0,48.0))
        */
}
