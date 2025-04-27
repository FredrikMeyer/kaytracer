package geometry

import net.fredrikmeyer.geometry.Vector3D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset
import org.junit.jupiter.api.Test
import java.util.function.Consumer

class Vector3DTest {
    @Test
    fun `normalize vector`() {
        val v = Vector3D(1f, 2f, 3f)
        val res = v.normalize()

        assertThat(res.norm()).isCloseTo(1f, Offset.offset(0.00001f))
    }

    @Test
    fun `cross product`() {
        val res = Vector3D.X cross Vector3D.Y

        assertThat(Vector3D.X cross Vector3D.Y).isEqualTo(Vector3D.Z)
        assertThat(Vector3D.Y cross Vector3D.X).satisfies(Consumer {
            assertThat(it.x).isEqualTo(0f)
            assertThat(it.y).isEqualTo(0f)
            assertThat(it.z).isEqualTo(-1f)
        })
        assertThat(Vector3D.Z cross Vector3D.Y).satisfies(Consumer {
            assertThat(it.x).isEqualTo(-1f)
            assertThat(it.y).isEqualTo(0f)
            assertThat(it.z).isEqualTo(0f)
        })
    }

    @Test
    fun `round to 0-1 vector`() {
        val p = Vector3D(1f, 0.7f, 0.3f)
        val c = Vector3D(0.5f, 0.5f, 0.5f)

        val d = p - c;

        val res = d.round()

        assertThat(res.x).isEqualTo(1.0f)
        assertThat(res.y).isEqualTo(0.0f)
        assertThat(res.z).isEqualTo(0.0f)
    }
}