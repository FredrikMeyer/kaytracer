import net.fredrikmeyer.Vector3D
import net.fredrikmeyer.dot
import org.junit.jupiter.api.Assertions.*
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