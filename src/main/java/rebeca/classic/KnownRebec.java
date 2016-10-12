/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rebeca.classic;

/**
 *
 * @author Yousefi
 */
public class KnownRebec{
    private Integer rebecID;
    private String name;
    
    public String getName(){
        return name;
    }
    
    public Integer getRebecID(){
        return rebecID;
    }
    
    public void setRebecID(int id){
        rebecID=id;
    }

    public KnownRebec(String name) {
        this.name=name;
        this.rebecID=-1;
    }
}