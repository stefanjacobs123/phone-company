package com.phone

package object model {

  /**
    * Each line in the calls.log file will be transformed into [[Call]] case classes.
    *
    * @param customerId   customer who made the call
    * @param numberCalled the number the customer called
    * @param duration     the duration of the call in seconds
    */
  case class Call(customerId: String, numberCalled: String, duration: Long)

  /**
    * A [[Bill]] is a bill for a particular [[Call]]
    *
    * @param customerId   customer who made the call
    * @param numberCalled the number the customer called
    * @param price        the price of the call in p
    */
  case class Bill(customerId: String, numberCalled: String, price: Double)



  /**
    * A [[DailyBill]] contains the daily bill for a customer.
    *
    * It's toString method is overridden to ease printing the total bill to cmdline.
    * Rounded to 2 decimals.
    *
    * @param customerId customer who made calls
    * @param billTotal  total bill for said calls (including/excluding promotion)
    */
  case class DailyBill(customerId: String, billTotal: Double) {
    override def toString: String = {
      s"Daily bill for customerId $customerId is ${BigDecimal(billTotal).setScale(2, BigDecimal.RoundingMode.HALF_UP)}p"
    }
  }

}
