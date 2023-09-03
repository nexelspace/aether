package aether.app.canvas

import aether.core.base.Base
import aether.core.platform.Event
import aether.core.platform.Module
import aether.core.platform.Platform
import aether.lib.canvas.Canvas
import aether.core.types.RectF
import aether.core.buffers.DynamicBuffer
import aether.core.platform.Dispatcher
import aether.core.types.Vec2I
import aether.core.graphics.Display
import aether.core.graphics.Graphics
import aether.core.platform.Log
import aether.core.input.KeyEvent
import aether.lib.canvas.shader.ShaderCanvas
import aether.lib.font.Font
import aether.core.network.WebSocket
import aether.core.input.KeyEvent.*
import aether.lib.canvas.shader.ShaderCanvas
import aether.lib.widget.WidgetEvents
import aether.core.types.Vec2F

class CanvasApp(val platform: Platform) extends Module {
  given Platform = platform
  given Dispatcher = platform.dispatcher
  val config = Config()
  
  val display = Display(config.dispSize, platform.name == Platform.Name.Js, windowTitle = "IkiCanvas")
  def size = display.size
  given g: Graphics = display.graphics

  val font = Font.default()

  // val config = res.config.get
  val state = new State()
  var ui: Option[CanvasWidget] = None // CanvasWidget(platform, state, size.toVec2F)
  var uiEvents: Option[WidgetEvents] = None

  val canvas = ShaderCanvas(g.size)

  val res = Resources(platform)
  res.allRes.onChange { r =>
    r.error.map(assert(false, _))
    ui = Some(CanvasWidget(platform, res, state, size.toVec2F))
    uiEvents = Some(new WidgetEvents(ui.get))
  }

  def event(event: Event) = {
    import KeyEvent.Code._
    event match {
      case e: Module.Init   => Log("Module.Init")
      case Module.Uninit    =>
      case e: Module.Update =>
      case Display.Paint(disp) =>
        g.clear(0xff004400)
        // val paintEvent = Canvas.Render(canvas, RectF(Vec2F.Zero, disp.size.toVec2F))
        // ui.event(paintEvent)
        ui.foreach(_.paint(canvas))
        font.get.foreach(font => canvas.drawString(Vec2F.Zero, font, "Hello World"))
        canvas.flush()
        // Logo.paint()
      case KeyPressed(S) =>
      case KeyPressed(L) =>
      case e =>
        uiEvents.foreach(_.event(e))
    }
  }

  // def create(factory: WidgetFactory, context: FactoryContext, widget: Pod): Option[Widget] = {
  //   widget.name match {
  //     case "canvas" =>
  //       canvas = Some(new CanvasWidget(widget, state, res, Display.primary.size.toVec2F))
  //       canvas
  //     case "console" => Some(new Console(res.font.get))
  //     case "palette" => Some(Palette(res.base, state, widget))
  //     case _         => baseFactory.create(factory, context, widget)
  //   }
  // }


}
