# Ductaper

Ductaper is a framework that is responsible for solving the problem of service to service communication in microservice architectures. Some of its prominent features are: 

  - Typesafe DSL for defining service endpoints
  - Asynchronous and message driven
  - Generic enough to allow for powerful extending
  
# Example Service
A typical DuctaperController looks like

```scala
object UserController extends DuctaperController {
  this: UserService =>

  override val endpoints = {


    unicast_endpoint at REGISTRATION_ENDPOINT takes input[RegistrationRequest] returns[RegistrationResponse] {
      (request) => {
        userService.register(x)
      }
    }

    ~

    unicast_endpoint at LOGIN_ENDPOINT takes input[LoginRequest] returns[Token] {
      (request) => {
        userService.issueToken(request)
      }
    }

    ~

    unicast_endpoint consumers 6 at LOGOUT_ENDPOINT takes input[LogoutRequest] returns[LogoutResult] {
      (request) => {
        userService.logout(request)
      }
    }

  }
}
```

# Usage
Typical usage is through extending the DuctaperController trait. In addition you need to provide rabbitMQ credentials.
One way to do that is through application.conf file: 

```yaml
rabbit {
  Uri = "amqp://localhost"
  username = "guest"
  password = "guest"
  topologyRecovery = "true"
  port = "5555"
  reconnectionStrategy = NoReconnect
}
```

The other way is to define the following environment variables:

```bash
RABBIT_URI
RABBIT_USERNAME
RABBIT_PASSWORD
RABBIT_PORT
RABBIT_REQUESTED_CHANNEL_MAX
RABBIT_REQUSTED_HEARTBEAT
RABBIT_REQUESTED_FRAME_MAX
RABBIT_CONNECTION_TIMEOUT
RABBIT_SHUTDOWN_TIMEOUT
RABBIT_TOPOLOGY_RECOVERY
RABBIT_RECONNECTION_STRATEGY
```
