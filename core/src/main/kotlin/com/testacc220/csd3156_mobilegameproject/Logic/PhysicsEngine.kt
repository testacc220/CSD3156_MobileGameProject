import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World


class ListenerClass : ContactListener {
    override fun beginContact(contact: Contact) {
        // Get the two colliding bodies
        val bodyA = contact.fixtureA.body
        val bodyB = contact.fixtureB.body

        Gdx.app.log("Contact Listener", "Begin Contact between bodies at: " +
            "A(${bodyA.position.x}, ${bodyA.position.y}) " +
            "B(${bodyB.position.x}, ${bodyB.position.y})")
    }

    override fun endContact(contact: Contact) {
        Gdx.app.log("Contact Listener", "End Contact")
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        Gdx.app.log("Contact Listener", "Pre Solve")
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        Gdx.app.log("Contact Listener", "Post Solve")
    }
}

class PhysicsEngine {
    private val world: World = World(Vector2(0f, -10f), true)
    private val bodies = mutableMapOf<Long, Body>()


    fun init()
    {
        Gdx.app.log("Physics System", "Physics init")
        world.setContactListener(ListenerClass())

        // Create ground
        val groundBodyDef = BodyDef()
        groundBodyDef.position.set(Vector2(0f, -20f))
        val groundBody = world.createBody(groundBodyDef)
        val groundBox = PolygonShape()
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(30.0f, 2.0f)

// Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f)

// Clean up after ourselves
        groundBox.dispose()
//
//
        // First we create a body definition (Create test body)
        val bodyDef = BodyDef()

// We set our body to dynamic, for something like ground which doesn't move we would set it to StaticBody
        bodyDef.type = BodyType.DynamicBody

// Set our body's starting position in the world
        bodyDef.position[5f] = 10f


// Create our body in the world using our body definition
        val body = world.createBody(bodyDef)


// Create a circle shape and set its radius to 6
        val circle = CircleShape()
        circle.radius = 6f


// Create a fixture definition to apply our shape to
        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 0.5f
        fixtureDef.friction = 0.4f
        fixtureDef.restitution = 0.6f // Make it bounce a little bit


// Create our fixture and attach it to the body
        val fixture = body.createFixture(fixtureDef)


// Remember to dispose of any shapes after you're done with them!
// BodyDef and FixtureDef don't need disposing, but shapes do.
        circle.dispose()

    }

    fun update(dt : Float)
    {
        world.step(dt, 6, 2)
        //Gdx.app.log("Physics System", "Physics update")
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

    fun updateGemPosition(uid: Long, x: Float, y: Float) {
        bodies[uid]?.setTransform(x, y, 0f)
    }

    fun createGemBody(uid: Long, x: Float, y: Float) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DynamicBody
        bodyDef.position.set(x, y)

        val body = world.createBody(bodyDef)
        val circle = CircleShape()
        circle.radius = 6f

        val fixtureDef = FixtureDef()
        fixtureDef.shape = circle
        fixtureDef.density = 0.5f
        fixtureDef.friction = 0.4f
        fixtureDef.restitution = 0.6f

        body.createFixture(fixtureDef)
        circle.dispose()

        bodies[uid] = body
    }

}
