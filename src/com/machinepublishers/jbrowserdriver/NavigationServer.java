/* 
 * jBrowserDriver (TM)
 * Copyright (C) 2014-2016 Machine Publishers, LLC
 * 
 * Sales and support: ops@machinepublishers.com
 * Updates: https://github.com/MachinePublishers/jBrowserDriver
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.machinepublishers.jbrowserdriver;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.machinepublishers.jbrowserdriver.Util.Pause;
import com.machinepublishers.jbrowserdriver.Util.Sync;

class NavigationServer extends UnicastRemoteObject implements NavigationRemote,
    org.openqa.selenium.WebDriver.Navigation {
  private final AtomicReference<JBrowserDriverServer> driver;
  private final Context context;
  private final AtomicInteger statusCode;

  NavigationServer(final AtomicReference<JBrowserDriverServer> driver,
      final Context context, final AtomicInteger statusCode)
          throws RemoteException {
    this.driver = driver;
    this.context = context;
    this.statusCode = statusCode;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void back() {
    Util.exec(Pause.SHORT, statusCode, ((TimeoutsServer) driver.get().manage().timeouts()).getPageLoadTimeoutMS(),
        new Sync<Object>() {
          public Object perform() {
            try {
              context.item().view.get().getEngine().getHistory().go(-1);
            } catch (IndexOutOfBoundsException e) {
              LogsServer.instance().exception(e);
            }
            return null;
          }
        });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void forward() {
    Util.exec(Pause.SHORT, statusCode, ((TimeoutsServer) driver.get().manage().timeouts()).getPageLoadTimeoutMS(),
        new Sync<Object>() {
          public Object perform() {
            try {
              context.item().view.get().getEngine().getHistory().go(1);
            } catch (IndexOutOfBoundsException e) {
              LogsServer.instance().exception(e);
            }
            return null;
          }
        });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh() {
    Util.exec(Pause.SHORT, statusCode, ((TimeoutsServer) driver.get().manage().timeouts()).getPageLoadTimeoutMS(),
        new Sync<Object>() {
          public Object perform() {
            context.item().view.get().getEngine().reload();
            return null;
          }
        });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void to(String url) {
    driver.get().get(url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void to(URL url) {
    driver.get().get(url.toExternalForm());
  }

}
