package com.example.birdrecognition

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.birdrecognition.databinding.FragmentHomeBinding
import com.example.birdrecognition.ml.Fullmodel2
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val imageSize = 224

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.imageSelect.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 3)
        }

        binding.birdSound.text?.toString()?.trim()?.let { userInputBirdName ->
            if (userInputBirdName.isNotBlank()) {
                // Fetch matching bird sound file names using the user-provided bird name
                getMatchingBirdSoundsFromStorage(userInputBirdName) { matchingBirdSounds ->
                    if (matchingBirdSounds.isNotEmpty()) {
                        // Play the first matching bird sound (You can choose how to handle multiple matches)
                        playBirdSoundFromStorage(matchingBirdSounds.first())
                    } else {
                        // Handle the case when no matching bird sound is found
                    }
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun classifyImage(image: Bitmap) {
        try {
            // Load the model
            val model = Fullmodel2.newInstance(requireContext())

            // Resize the image to the expected input size
            val resizedImage = Bitmap.createScaledBitmap(image, imageSize, imageSize, true)

            // Prepare the image
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(imageSize * imageSize)
            resizedImage.getPixels(intValues, 0, imageSize, 0, 0, imageSize, imageSize)

            // Resize and normalize the image
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val pixel = intValues[i * imageSize + j]
                    byteBuffer.putFloat(((pixel shr 16) and 0xFF) * (1f / 255))
                    byteBuffer.putFloat(((pixel shr 8) and 0xFF) * (1f / 255))
                    byteBuffer.putFloat((pixel and 0xFF) * (1f / 255))
                }
            }

            // Create TensorBuffer and load data
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Run inference
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val confidences = outputFeature0.floatArray


            val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: -1
            val resultText = if (maxPos >= 0 && confidences.isNotEmpty()) {
                classes[maxPos]
            } else {
                "No result"
            }

            // Show result in the TextView
            binding.text.text = resultText

            // Release model resources
            model.close()

        } catch (e: IOException) {
            Log.e("ModelError", "Error loading model", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 3) {
            data?.data?.let { imageUri ->
                try {
                    // Convert URI to Bitmap
                    binding.imageSelect.isVisible = false
                    binding.imageView1.setImageURI(imageUri)
                    val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                    // Call classifyImage with the selected Bitmap
                    classifyImage(imageBitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    // Handle the exception (e.g., show an error message)
                }
            }
        }
    }

    val classes = arrayOf(
        "ANNAS HUMMINGBIRD",
        "ANHINGA",
        "BALD EAGLE",
        "BALTIMORE ORIOLE",
        "ANTBIRD",
        "ARARIPE MANAKIN",
        "BARN OWL",
        "BANANAQUIT",
        "BARN SWALLOW",
        "BAY-BREASTED WARBLER",
        "BAR-TAILED GODWIT",
        "BLACK SKIMMER",
        "BLACK THROATED WARBLER",
        "BIRD OF PARADISE",
        "BLACK FRANCOLIN",
        "BLACK SWAN",
        "BELTED KINGFISHER",
        "BLUE GROUSE",
        "BLACK-CAPPED CHICKADEE",
        "BLUE HERON",
        "BLACK-NECKED GREBE",
        "BOBOLINK",
        "BLACK VULTURE",
        "BLACKBURNIAM WARBLER",
        "CANARY",
        "CALIFORNIA GULL",
        "CALIFORNIA QUAIL",
        "CALIFORNIA CONDOR",
        "BROWN THRASHER",
        "CACTUS WREN",
        "CAPE MAY WARBLER",
        "CASPIAN TERN",
        "CARMINE BEE-EATER",
        "CINNAMON TEAL",
        "CHARA DE COLLAR",
        "CASSOWARY",
        "COCK OF THE ROCK",
        "CHIPPING SPARROW",
        "COMMON GRACKLE",
        "COMMON HOUSE MARTIN",
        "COMMON LOON",
        "COCKATOO",
        "CROW",
        "COMMON POORWILL",
        "CRESTED CARACARA",
        "COMMON STARLING",
        "COUCHS KINGBIRD",
        "CRESTED AUKLET",
        "CURL CRESTED ARACURI",
        "CROWNED PIGEON",
        "CUBAN TODY",
        "DARK EYED JUNCO",
        "DOWNY WOODPECKER",
        "D-ARNAUDS BARBET",
        "EASTERN BLUEBIRD",
        "DOVEKIE",
        "ELLIOTS PHEASANT",
        "EASTERN TOWEE",
        "EASTERN MEADOWLARK",
        "ELEGANT TROGON",
        "EMPEROR PENGUIN",
        "EASTERN ROSELLA",
        "FRIGATE",
        "EURASIAN MAGPIE",
        "EVENING GROSBEAK",
        "FLAMINGO",
        "EMU",
        "FLAME TANAGER",
        "GILA WOODPECKER",
        "GOLDEN CHLOROPHONIA",
        "GOLD WING WARBLER",
        "GOLDEN EAGLE",
        "GRAY CATBIRD",
        "GOULDIAN FINCH",
        "GOLDEN PHEASANT",
        "GLOSSY IBIS",
        "HAWAIIAN GOOSE",
        "GRAY PARTRIDGE",
        "GREY PLOVER",
        "HOOPOES",
        "GUINEAFOWL",
        "HOODED MERGANSER",
        "HORNBILL",
        "GREEN JAY",
        "INDIGO BUNTING",
        "HOUSE SPARROW",
        "JABIRU",
        "HYACINTH MACAW",
        "JAVAN MAGPIE",
        "HOUSE FINCH",
        "INCA TERN",
        "KING VULTURE",
        "LONG-EARED OWL",
        "MALEO",
        "MALLARD DUCK",
        "KILLDEAR",
        "LARK BUNTING",
        "LILAC ROLLER",
        "MOURNING DOVE",
        "NICOBAR PIGEON",
        "MIKADO PHEASANT",
        "MYNA",
        "MARABOU STORK",
        "MANDRIN DUCK",
        "MASKED BOOBY",
        "OCELLATED TURKEY",
        "NORTHERN RED BISHOP",
        "NORTHERN FLICKER",
        "NORTHERN GOSHAWK",
        "NORTHERN JACANA",
        "NORTHERN CARDINAL",
        "NORTHERN GANNET",
        "NORTHERN MOCKINGBIRD",
        "PEACOCK",
        "PARUS MAJOR",
        "PARADISE TANAGER",
        "PAINTED BUNTING",
        "OSPREY",
        "PELICAN",
        "OSTRICH",
        "PEREGRINE FALCON",
        "RAINBOW LORIKEET",
        "PURPLE FINCH",
        "QUETZAL",
        "RED FACED CORMORANT",
        "PURPLE SWAMPHEN",
        "PINK ROBIN",
        "PURPLE MARTIN",
        "PUFFIN",
        "PURPLE GALLINULE",
        "RED WISKERED BULBUL",
        "ROBIN",
        "RING-NECKED PHEASANT",
        "ROCK DOVE",
        "RED HEADED WOODPECKER",
        "RED THROATED BEE EATER",
        "RED WINGED BLACKBIRD",
        "ROADRUNNER",
        "SCARLET MACAW",
        "RUFOUS KINGFISHER",
        "RUFOUS MOTMOT",
        "SCARLET IBIS",
        "SAND MARTIN",
        "ROSY FACED LOVEBIRD",
        "RUBY THROATED HUMMINGBIRD",
        "ROUGH LEG BUZZARD",
        "SHOEBILL",
        "SORA",
        "SPLENDID WREN",
        "SPOONBILL",
        "STRAWBERRY FINCH",
        "STORK BILLED KINGFISHER",
        "SNOWY EGRET",
        "TURQUOISE MOTMOT",
        "TRUMPTER SWAN",
        "TIT MOUSE",
        "TURKEY VULTURE",
        "TAIWAN MAGPIE",
        "TEAL DUCK",
        "TOUCHAN",
        "WILSONS BIRD OF PARADISE",
        "VARIED THRUSH",
        "VIOLET GREEN SWALLOW",
        "VENEZUELAN TROUPIAL",
        "WILD TURKEY",
        "WESTERN MEADOWLARK",
        "WHITE TAILED TROPIC",
        "VERMILION FLYCATHER",
        "WHITE CHEEKED TURACO",
        "WOOD DUCK",
        "YELLOW HEADED BLACKBIRD",
        "ALBATROSS",
        "ALEXANDRINE PARAKEET",
        "AMERICAN AVOCET",
        "AMERICAN BITTERN",
        "AMERICAN COOT",
        "AMERICAN GOLDFINCH",
        "AMERICAN KESTREL",
        "AMERICAN PIPIT",
        "AMERICAN REDSTART"
    )

    fun getMatchingBirdSoundsFromStorage(birdName: String, onResult: (List<String>) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val birdSoundsRef: StorageReference = storageRef.child("bird_sounds")

        birdSoundsRef.listAll().addOnSuccessListener { listResult ->
            val matchingBirdSounds = listResult.items.filter { item ->
                item.name.contains(birdName, ignoreCase = true)
            }.map { item ->
                item.name
            }

            onResult(matchingBirdSounds)
        }.addOnFailureListener {
            // Handle failure to fetch the bird sound file names
            onResult(emptyList())
        }
    }

    fun playBirdSoundFromStorage(birdName: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val soundRef: StorageReference = storageRef.child("bird_sounds/$birdName.mp3")

        val mediaPlayer = MediaPlayer()
        soundRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                }
                mediaPlayer.setOnCompletionListener {
                    mediaPlayer.release()
                }
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.addOnFailureListener {
            // Handle failure to download the sound file or invalid bird name
        }
    }
}
