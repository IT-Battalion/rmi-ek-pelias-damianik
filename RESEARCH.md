### Was ist RMI und welches Prinzip der verteilten Programmierung kommt dabei zur Anwendung?

The Java Remote Method Invocation (RMI) system allows an object running  in one Java virtual machine to invoke methods on an object running in  another Java virtual machine. RMI provides for remote communication  between programs written in the Java programming language. [1]

At the most basic level, RMI is Java's remote procedure call (RPC) mechanism. [2]

### Was sind Stubs? Welche Aufgabe hat dabei das Proxy-Objekt?

RMI treats a remote object differently from a non-remote object when the object is passed from one Java virtual machine to another Java virtual  machine. Rather than making a copy of the implementation object in the  receiving Java virtual machine, RMI passes a remote *stub* for a  remote object. The stub acts as the local representative, or proxy, for  the remote object and basically is, to the client, the remote reference. The client invokes a method on the local stub, which is responsible for carrying out the method invocation on the remote object. [...] Rather than making a copy of the implementation object in the receiving Java virtual machine, RMI passes a remote *stub* for a remote object. The stub acts as the local representative, or  proxy, for the remote object and basically is, to the client, the remote reference. The client invokes a method on the local stub, which is  responsible for carrying out the method invocation on the remote object. [3]

### Was wird in der Registry gespeichert?

Applications can use various mechanisms to obtain references to remote  objects. For example, an application can register its remote objects  with RMI's simple naming facility, the RMI registry. Alternatively, an  application can pass and return remote object references as part of  other remote invocations. [...] The server calls the registry to associate (or bind) a name with a  remote object. The client looks up the remote object by its name in the  server's registry and then invokes a method on it. [3]

### Wie kommt das `Remote`-Interface zum Einsatz? Was ist bei der Definition von Methoden zu beachten?

Like any other Java application, a distributed application built by  using Java RMI is made up of interfaces and classes. The interfaces  declare methods. The classes implement the methods declared in the  interfaces and, perhaps, declare additional methods as well. In a  distributed application, some implementations might reside in some Java  virtual machines but not others. Objects with methods that can be  invoked across Java virtual machines are called *remote objects*.

An object becomes remote by implementing a *remote interface*, which has the following characteristics:

- A remote interface extends the interface `java.rmi.Remote`.
- Each method of the interface declares `java.rmi.RemoteException` in its `throws` clause, in addition to any application-specific exceptions.

RMI treats a remote object differently from a non-remote object when  the object is passed from one Java virtual machine to another Java  virtual machine. [...] A stub for a remote object implements the same set of remote interfaces  that the remote object implements. This property enables a stub to be  cast to any of the interfaces that the remote object implements.  However, *only* those methods defined in a remote interface are available to be called from the receiving Java virtual machine. [3]

### Was ist bei der Weitergabe von Objekten unabdingbar?

Distributed object applications need to do the following:

- **Locate remote objects.** Applications can use various  mechanisms to obtain references to remote objects. For example, an  application can register its remote objects with RMI's simple naming  facility, the RMI registry. Alternatively, an application can pass and  return remote object references as part of other remote invocations.
- **Communicate with remote objects.** Details of communication  between remote objects are handled by RMI. To the programmer, remote  communication looks similar to regular Java method invocations.
- **Load class definitions for objects that are passed around.**  Because RMI enables objects to be passed back and forth, it provides  mechanisms for loading an object's class definitions as well as for  transmitting an object's data. [3]

### Welche Methoden des `UnicastRemoteObject` kommen bei der Server-Implementierung zum Einsatz?

The method 'java.rmi.Remote exportObject(java.rmi.Remote obj, int port)'. According to the Java documentation:

Exports the remote object to make it available to receive incoming calls, using the particular supplied port.  

The object is exported with a server socket created using the [`RMISocketFactory`](https://docs.oracle.com/javase/8/docs/api/java/rmi/server/RMISocketFactory.html) class. [4]

### Wie kann der Server ein sauberes Schließen ermöglichen? Was muss mit dem exportierten Objekt geschehen?

The exported objects must be unbound from the registry (with `Registry#unbind(String name)`) and the object itself must be unexported (with `UnicastRemoteObject#unexportObject(java.rmi.Remote obj, bool force)`)

### Quellen

[1] "Oracle"; "Trail: RMI (The Java&trade; Tutorials)"; zuletzt besucht am 24.05.2022; https://docs.oracle.com/javase/tutorial/rmi/

[2] "Oracle"; "Java Remote Method Invocation  Distributed Computing for Java"; zuletzt besucht am 24.05.2022; https://www.oracle.com/java/technologies/javase/remote-method-invocation-distributed-computing.html

[3] "Oracle"; "An Overview of RMI Applications (The Java&trade; Tutorials &gt; RMI)"; zuletzt besucht am 24.05.2022; https://docs.oracle.com/javase/tutorial/rmi/overview.html

[4] "Oracle"; "UnicastRemoteObject (Java Platform SE 8 )"; zuletzt besuch am 24.05.2022; https://docs.oracle.com/javase/8/docs/api/java/rmi/server/UnicastRemoteObject.html#exportObject-java.rmi.Remote-
