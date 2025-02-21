import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import java.io.Console


class ListenerClass : ContactListener {
    override fun endContact(contact: Contact) {
    }

    override fun beginContact(contact: Contact) {
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        TODO("Not yet implemented")
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        TODO("Not yet implemented")
    }


}

class PhysicsEngine {
    private val world: World = World(Vector2(0f, -10f), true)
    

    fun init()
    {
        world.setContactListener(ListenerClass())
    }

    fun update(dt : Float)
    {
        world.step(dt, 6, 2)
        Gdx.app.log("Physics System", "Physics update")
    }

    fun createBody(pos: Vector2)
    {
        val bodyDef :BodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(pos)

        val body : Body = world.createBody(bodyDef)

        val circle : CircleShape = CircleShape()
        circle.radius = 6f

        val fixtureDef : FixtureDef = FixtureDef()
        fixtureDef.shape = circle

        val fixture : Fixture = body.createFixture(fixtureDef)

        circle.dispose()
    }
}
