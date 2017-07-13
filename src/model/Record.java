/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author asus
 */
public class Record {
    
    private final String accountNo;
    private final String accountOwner;
    private final String date;
    private final String hour;
    
    //datetime is in the form of "dd MMMM yyyy,HH:mm"
    public Record(String accNo, String owner, String datetime){
        this.accountNo = accNo;
        this.accountOwner = owner;
        String[] arr = datetime.split(",");
        this.date = arr[0];
        this.hour = arr[1];
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }
    
    
    
}
