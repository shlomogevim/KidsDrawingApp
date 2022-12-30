package com.sg.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null
    private var mImageButtonCurrentPaint: ImageButton? = null
    var custumDialog: Dialog? = null
    val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //Todo 3: get the returned result from the lambda and check the resultcode and the data returned
            if (result.resultCode == RESULT_OK && result.data != null) {
                //process the data
                //Todo 4 if the data is not null reference the imageView from the layout
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                //Todo 5: set the imageuri received
                imageBackground.setImageURI(result.data?.data)
            }
        }

    //   lateinit var linearlayoutPaintColor: LinearLayout

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val perMissionName = it.key
                val isGranted = it.value
                //Todo 3: if permission is granted show a toast and perform operation
                if (isGranted) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission granted now you can read the storage files.",
                        Toast.LENGTH_SHORT
                    ).show()
                  /*  val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)*/

                } else {
                    //Todo 4: Displaying another toast if permission is not granted and this time focus on
                    //    Read external storage
                    if (perMissionName == Manifest.permission.READ_EXTERNAL_STORAGE)
                        Toast.makeText(
                            this@MainActivity,
                            "Oops you just denied the permission.",
                            Toast.LENGTH_LONG
                        ).show()
                }
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawig_view_layout)
        val linearlayoutPaintColor: LinearLayout = findViewById(R.id.ll_paint_colors)

        drawingView?.setSigeForBrush(20f)

        mImageButtonCurrentPaint = linearlayoutPaintColor[4] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pressed)

        )
        val ib_brush: ImageButton = findViewById(R.id.ib_action_buttons)
        ib_brush.setOnClickListener {
            showBrushSizeChoserDialog()
        }
        val backgroundScreen:ImageView=findViewById(R.id.iv_background)
        val galleryImage=registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
               backgroundScreen.setImageURI(it)
        })
        val ib_gallery: ImageButton = findViewById(R.id.ib_gallery)
        ib_gallery.setOnClickListener {
            galleryImage.launch("image/*")
           // requestStoragePermission()
        }
        val id_undo: ImageButton = findViewById(R.id.ib_undo)
        id_undo.setOnClickListener {
            drawingView!!.onClickUndo()
        }
        val ibSave: ImageButton = findViewById(R.id.ib_save)
        ibSave.setOnClickListener {
            if (isReadStorageAllable()) {
                showProgressDialog()
                lifecycleScope.launch {
                    val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    saveBitmapeFile(getBitmapFromView(flDrawingView))

                }
            }
        }
    }

    private fun isReadStorageAllable(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        //Todo 6: Check if the permission was denied and show rationale
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            //Todo 9: call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog(
                "Kids Drawing App", "Kids Drawing App " +
                        "needs to Access Your External Storage"
            )
        } else {
            // You can directly ask for the permission.
            // Todo 7: if it has not been denied then request for permission
            //  The registered ActivityResultCallback gets the result of this request.
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }

    private fun showBrushSizeChoserDialog() {
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size :  ")
        val brush102 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_2)
        brush102.setOnClickListener {
            drawingView?.setSigeForBrush(2f)
            brushDialog.dismiss()
        }
        val brush105 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_5)
        brush105.setOnClickListener {
            drawingView?.setSigeForBrush(5f)
            brushDialog.dismiss()
        }
        val brush110 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_10)
        brush110.setOnClickListener {
            drawingView?.setSigeForBrush(10f)
            brushDialog.dismiss()
        }
        val brush115 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_15)
        brush115.setOnClickListener {
            drawingView?.setSigeForBrush(15f)
            brushDialog.dismiss()
        }
        val brush120 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_20)
        brush120.setOnClickListener {
            drawingView?.setSigeForBrush(20f)
            brushDialog.dismiss()
        }
        val brush125 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_25)
        brush125.setOnClickListener {
            drawingView?.setSigeForBrush(25f)
            brushDialog.dismiss()
        }
        val brush130 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_30)
        brush130.setOnClickListener {
            drawingView?.setSigeForBrush(30f)
            brushDialog.dismiss()
        }
        val brush135 = brushDialog.findViewById<ImageButton>(R.id.ib_brush_35)
        brush135.setOnClickListener {
            drawingView?.setSigeForBrush(35f)
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View) {
        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.pressed)
            )
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.normal)
            )
            mImageButtonCurrentPaint = view
        }
    }

    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnBitmap
    }

    private suspend fun saveBitmapeFile(mBitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
                    val fName = File(
                        getExternalFilesDir(null),
                        "MyPaint_" + (System.currentTimeMillis() / 1000) + ".jpg"
                    )
                    val fOutputStream = FileOutputStream(fName)
                    fOutputStream.write(bytes.toByteArray())
                    fOutputStream.close()
                    result = fName.absolutePath

                    /*   val f=File(externalCacheDir?.absoluteFile.toString()+File.separator+"KidDrawingApp"+System.currentTimeMillis()/1000+".png")
                       val fo=FileOutputStream(f)
                       fo.write(bytes.toByteArray())
                       fo.close()
                       result=f.absolutePath*/

                    runOnUiThread {
                        cancelProgressDialog()
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File save successuffly : $result", Toast.LENGTH_SHORT
                            ).show()

                            shareImage(
                                FileProvider.getUriForFile(
                                    baseContext,
                                    "com.sg.kidsdrawingapp.fileprovider",
                                    fName
                                )
                            )

                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Something Worong in saving Png",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun shareImage(uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
        }
        startActivity(Intent.createChooser(shareIntent, "Share image via "))
    }

    private fun showProgressDialog() {
        custumDialog = Dialog(this)
        custumDialog?.setContentView(R.layout.dialog_custom_dialog)
        custumDialog?.show()

    }

    private fun cancelProgressDialog() {
        if (custumDialog != null) {
            custumDialog?.dismiss()
            custumDialog = null
        }
    }
}


/*   private fun shareImage(result:String){
       MediaScannerConnection.scanFile(this, arrayOf(result),null){
           path,uri ->
           val shareIntent=Intent()
           shareIntent.action=Intent.ACTION_SEND
           shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
           shareIntent.type="image/png"
           startActivity(Intent.createChooser(shareIntent,"Share"))
       }
   }*/
/*   private suspend fun saveBitmapeFile(mBitmap:Bitmap?):String{
        var result=""
        withContext(Dispatchers.IO){
            if (mBitmap!=null){
                try {
                    val bytes=ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                    val f=File(externalCacheDir?.absoluteFile.toString()+File.separator+"KidDrawingApp"+System.currentTimeMillis()/1000+".png")
                    val fo=FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result=f.absolutePath

                    runOnUiThread {
                        cancelProgressDialog()
                        if (result.isNotEmpty()){
                              Toast.makeText(this@MainActivity,
                                  "File save successuffly : $result",Toast.LENGTH_SHORT
                              ).show()

                            shareImage(FileProvider.getUriForFile(baseContext,"com.sg.kidsdrawingapp.fileprovider",f))
                                                                                                                                  //   com.sg.kidsdrawingapp/files



                          // shareImage(result)
                           // setUpEnablingFeatures(FileProvider.getUriForFile("com.sg.kidsdrawingapp",))
                        }else{
                            Toast.makeText(this@MainActivity,"Something Worong in saving Png",Toast.LENGTH_SHORT).show()

                        }
                    }

                }catch (e:Exception){
                    result=""
                    e.printStackTrace()
                }
            }
        }
        return result
    }*/


/* private fun setUpEnablingFeatures(uri: Uri){
     val intent = Intent()
     intent.action = Intent.ACTION_SEND
     intent.putExtra(Intent.EXTRA_STREAM, uri)
     intent.type = "image/jpeg"
     startActivity(Intent.createChooser(intent, "Share image via "))

 }*/

