# zioDi
Example of DI with ZIO

```
object VerticalComposition {
  class Service(x: x.Service, y: y.Service) {
    def func():Task[X] = {
      for {
        combine services...
      } yield()
    }
  }
}

object HorizantalComposition {
  trait Service {
    def func():Task[X] = {...}
  }
}
```
