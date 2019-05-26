package utils

object Constants {

}

object CurrencyCode extends Enumeration {
  type CurrencyCode = Value
  val INR = Value("INR")
}

object TransferStatus extends Enumeration {
  type CurrencyCode = Value
  val Success = Value("Transfer successful")
  val InsufficentFund = Value("Transfer failed due to insufficient fund")
  val Failed = Value("Transfer failed")
}