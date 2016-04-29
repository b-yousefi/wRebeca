package wRebeca.common;

import java.util.HashMap;
import java.util.Map;


public class hashMap
    {
        Map<glState, Integer> tablee;
        public  Object lock_put = new Object();
//        public Map<glState, Integer> getTablee()
//        {
//            return tablee; 
//        }
        
        public glState getGlState(int stNum)
        {
        	for(glState item :tablee.keySet())
        	{
        		if(tablee.get(item)==stNum)
        			return item;
        	}
        	return null;
        }
        Integer state_number;

        public Integer table_size()
        {
            return tablee.size();
        }

        public Integer get_stNumber(glState gl)
        {
           // glState gl = new glState(states);

            Integer stNumber=-1 ;
        	synchronized (lock_put)
            {
        		stNumber=tablee.get(gl);
            }
            if(stNumber==null)
                stNumber = -1;
            //try
            //{
            //    stNumber =(Integer) table[gl];
            //}
            //catch (Exception)
            //{
                
            //    //throw;
            //}
            return stNumber;
        }

//        private Map<State, Integer> create_key(Map<State, List<Integer>> globalState)
//        {
//            Map<State, Integer> resultState = new HashMap<State, Integer>();
//            for (State item : globalState.keySet())
//            {
//                if (resultState.containsKey(item))
//                	  resultState.put(item, resultState.get(item)+1);
//                else
//                    resultState.put(item, globalState.get(item).size());
//            }
//            return resultState;
//        }

        public Integer insert(glState gl)
        {
            Integer stNum=-1;
            stNum = get_stNumber(gl);
            if (stNum == -1)
            {
            	synchronized (lock_put)
                {
	                glState gll = gl.deepCopy();
	                if(tablee.put(gll, state_number)==null)
	                {
		                stNum = state_number;
		                state_number++;
	                }
	                else
	                {
	                	 stNum = state_number;
	                }
                }
            }
            return stNum;
        }
        public hashMap(Integer start)
        {
            state_number = start;
            tablee = new HashMap<glState, Integer>();
        }
    }

