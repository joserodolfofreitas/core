package org.jboss.weld.test.unit.bootstrap.multipleEnterpriseAndSimple;

import javax.ejb.Remove;
import javax.ejb.Stateful;

@Stateful
@Synchronous
public class Tiger implements TigerLocal
{
   
   @Remove
   public void remove()
   {
      
   }

}