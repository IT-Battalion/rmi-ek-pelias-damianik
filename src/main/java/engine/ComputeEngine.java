/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package engine;

import java.io.*;
import java.rmi.AccessException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import balance.Balance;
import balance.BalanceItem;
import compute.Compute;
import compute.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputeEngine implements Compute, BalanceItem {
    private int activeConnections;

    public ComputeEngine() {
        super();
    }

    public <T> T executeTask(Task<T> t) {
        log.debug("Started executing a task");
        this.activeConnections++;
        T result = t.execute();
        this.activeConnections--;
        log.debug("Finished executing a task");
        return result;
    }

    @Override
    public int getWeight() {
        return activeConnections;
    }

    private static Logger log = LoggerFactory.getLogger(ComputeEngine.class);

    public static void main(String[] args) {
        log.info("Starting Compute Engine");
        System.setProperty("java.security.policy", "./security.policy");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        String name = "Balance";
        Registry registry = null;
        Compute stub = null;
        try {
            Compute engine = new ComputeEngine();
            stub = (Compute)
                    UnicastRemoteObject.exportObject(engine, 0);
            log.debug("Getting Registry for " + args[0]);
            registry = LocateRegistry.getRegistry(args[0]);
            Balance balancer = (Balance) registry.lookup(name);
            balancer.register(stub);
            log.info("ComputeEngine bound.");

            try (BufferedInputStream inputStream = new BufferedInputStream(System.in)) {
                inputStream.read();
            } finally {
                balancer.unregister(stub);
            }
        } catch (AccessException e) {
            log.error("Operation is not permitted.");
        } catch (NotBoundException e) {
            log.error("The Name is currently not bound.");
        } catch (RemoteException e) {
            log.error("Registry Reference could not be created.");
        } catch (IOException e) {
            log.error("Reading Input failed", e);
        } finally {
            if (registry != null) {
                try {
                    registry.unbind(name);
                } catch (RemoteException | NotBoundException ignored) {
                }
            }
            if (stub != null) {
                try {
                    UnicastRemoteObject.unexportObject(stub, false);
                } catch (NoSuchObjectException ignored) {
                }
            }
        }
    }
}
