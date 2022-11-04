/**
 * FurthestInFuture
 * Assignment: HW 4 CS577
 * Author: Jason Lee (jlee967@wisc.edu)
 * 
 */

import java.util.*;

public class FIF
{
    private String [] request;
    private int pageNum = 0; // Number of pages that have already been requested
    private int pageFault = 0; // Result
    private int cacheSize; 

    private Hashtable <String, Integer> ht = new Hashtable <String, Integer>();  
    private Hashtable <String, Integer> cache = new Hashtable <String, Integer>();

    private PriorityQueue<Integer> priorityCache = new PriorityQueue<Integer>(Collections.reverseOrder());

    private int [] steps;

    // FIF Constructor
    public FIF(int cacheNum, int requestNum)
    {
        this.cacheSize = cacheNum;
        this.request = new String[requestNum];
        this.steps = new int[requestNum];
    }

    // Checks if we have a page fault or not.
    public boolean isFault(String newRequest)
    {
        if(!cache.containsKey(newRequest))
        {
            return true;
        }

        return false;
    }

    
    public void setSequence(String sequenceRequest)
    {
        this.request = sequenceRequest.trim().split(" ");
    }

    // Finds key in hashtable and returns key string
    public String findKey(int keyValue)
    {
        for(Object o : cache.keySet())
        {
            if(cache.get(o).equals(keyValue))
            {
                return o.toString();
            }
        }

        return "shit broke";
    }


    /**
     * Creates an array to store steps
     * Starts from the end
     * if no key/page match, add to hash table
     * if key/page match, update value to the number of steps we encounter
     * */
    public void findSteps()
    {
        for(int i = this.request.length - 1; i >= 0; i--)
        {
            if(!ht.containsKey(this.request[i]))
            {
                ht.put(this.request[i], i);
                steps[i] = Integer.MAX_VALUE / 2;
            }

            else
            {
                steps[i] = ht.get(this.request[i]) - i;
                ht.put(this.request[i], i);
            }
        }
    }


    public void getNextPage()
    {
        if(cache.size() < cacheSize)
        {
            if(this.isFault(request[pageNum]))
            {
                pageFault++;
                cache.put(request[pageNum], steps[pageNum] + pageNum); // Adds page and a step to the cache
                priorityCache.add(steps[pageNum] + pageNum); // Adds step to heap
            }

            else
            {
                Iterator<Integer> it1 = priorityCache.iterator();

                while(it1.hasNext())
                {
                    if((int)it1.next() == cache.get(request[pageNum]))
                    {
                        it1.remove();
                        break;
                    }
                }

                priorityCache.add(steps[pageNum] + pageNum);
                cache.put(request[pageNum], steps[pageNum] + pageNum);

            }
            return;
        }

        if(this.isFault(request[pageNum]))
        {
            pageFault++;

            String toRemove = this.findKey(priorityCache.poll()); // remove page with largest amount of steps
            cache.remove(toRemove);

            cache.put(request[pageNum], steps[pageNum] + pageNum);
            priorityCache.add(steps[pageNum] + pageNum);
        }

        else // if no fault, update
        {
            Iterator <Integer>  it2 = priorityCache.iterator();

            while(it2.hasNext())
            {
                if((int)it2.next() == cache.get(request[pageNum]))
                {
                    it2.remove();
                    break;
                }
            }

            priorityCache.add(steps[pageNum] + pageNum);
            cache.put(request[pageNum], steps[pageNum] + pageNum);
        }
    }

    public int getFaults()
    {
        while(pageNum < request.length)
        {
            this.getNextPage();
            pageNum++;
        }

        return pageFault;
    }


    public static void main(String [] args)
    {
        ArrayList <FIF> instances = new ArrayList <FIF>();

        Scanner kb = new Scanner(System.in);
        String numLines = kb.nextLine().trim();
        int numInstances = Integer.parseInt(numLines);

        for(int i = 0; i < numInstances; i++)
        {
            numLines = kb.nextLine().trim();
            int inCache = Integer.parseInt(numLines);

            numLines = kb.nextLine().trim();
            int reqNum = Integer.parseInt(numLines);
            instances.add(new FIF(inCache, reqNum));

            numLines = kb.nextLine().trim();
            instances.get(i).setSequence(numLines);
        }

        for(int j = 0; j < numInstances; j++)
        {
            instances.get(j).findSteps();

            System.out.println(instances.get(j).getFaults());
        }

        kb.close();
    }
}