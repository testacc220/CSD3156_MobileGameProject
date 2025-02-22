import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
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
        //Gdx.app.log("Contact Listener", "End Contact")
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        //Gdx.app.log("Contact Listener", "Pre Solve")
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        //Gdx.app.log("Contact Listener", "Post Solve")
    }
}

class PhysicsEngine {
    private val world: World = World(Vector2(0f, -20f), true)
    private val bodies = mutableMapOf<Long, Body>()
    private var debugRenderer: Box2DDebugRenderer = Box2DDebugRenderer()

    fun init(screenWidth: Float)
    {
        Gdx.app.log("Physics System", "Physics init")
        world.setContactListener(ListenerClass())


        // Create our body definition
        val groundBodyDef = BodyDef()
// Set its world position
        groundBodyDef.position.set(Vector2(screenWidth / 2, 0f))
// Create a body from the definition and add it to the world
        val groundBody = world.createBody(groundBodyDef)
// Create a polygon shape
        val groundBox = PolygonShape()
// Set the polygon shape as a box which is twice the size of our view port and 20 high
// (setAsBox takes half-width and half-height as arguments)
        groundBox.setAsBox(screenWidth / 2, 5.0f)
// Create a fixture from our polygon shape and add it to our ground body
        groundBody.createFixture(groundBox, 0.0f)
// Clean up after ourselves
        groundBox.dispose()
    }

    fun update(dt : Float)
    {
        world.step(dt, 6, 2)
        //Gdx.app.log("Physics System", "Physics update")
    }

    fun draw(projMat : Matrix4)
    {
        debugRenderer.render(world, projMat);
    }

    fun createBody(pos: Vector2, id : Long)
    {
        val bodyDef :BodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(pos)

        val body : Body = world.createBody(bodyDef)

        val boxShape = PolygonShape()
        boxShape.setAsBox(32f, 32f)

        val fixture : Fixture = body.createFixture(boxShape, 0.0f)

        boxShape.dispose()
    }

    fun updateGemPosition(uid: Long, x: Float, y: Float) {
        bodies[uid]?.setTransform(x, y, 0f)
    }

    fun getGemPosition(uid : Long) : Vector2
    {
        return bodies[uid]?.position!!
    }


    fun createGemBody(uid: Long, x: Float, y: Float) {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.DynamicBody
        bodyDef.position.set(x, y)

        val body = world.createBody(bodyDef)
        val boxShape = PolygonShape()
        boxShape.setAsBox(32f, 32f)

        val fixture : Fixture = body.createFixture(boxShape, 0.0f)

        //body.createFixture(fixtureDef)
        boxShape.dispose()

        bodies[uid] = body
    }

}
