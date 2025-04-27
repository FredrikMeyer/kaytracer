import net.fredrikmeyer.geometry.Vector3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VectorKtTest {
     @Test
     fun `dot two vectors`() {
         val v1 = Vector3D(1f, 2f, 3f)
         val v2 = Vector3D(2f, 3f, 4f)

         val res = v1 dot v2

         assertEquals(20f, res)
     }
 }