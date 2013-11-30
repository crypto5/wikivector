package org.wikivector.misc

import org.wikivector.api.MetaDataExtractor
import java.net.URL
import scala.collection.JavaConversions._

object MetadataExtractorTest extends App {
  extract("""
      Bitcoin (sign: BitcoinSign.svg; code: BTC or XBT[1]) is a peer-to-peer digital currency that functions without the intermediation of a central authority.[2] The concept was introduced in a 2008 paper by a pseudonymous developer known as "Satoshi Nakamoto".[3]

Bitcoin has been called a cryptocurrency because it is decentralized and uses cryptography to control transactions and prevent double-spending, a problem for digital currencies.[2] Once validated, every individual transaction is permanently recorded in a public ledger known as the blockchain.[2] Payment processing is done by a network of private computers often specially tailored to this task.[4] The operators of these computers, known as "miners", are rewarded with transaction fees and newly minted Bitcoins. However, new bitcoins are created at an ever-decreasing rate.[2]

In 2012, The Economist reasoned that Bitcoin has been popular because of "its role in dodgy online markets,"[5] and in 2013 the FBI shut down one such service, Silk Road, which specialized in illegal drugs (whereupon the FBI came into the control of approximately 1.5% of all bitcoins in circulation).[6] However, bitcoins are increasingly used as payment for legitimate products and services, and merchants have an incentive to accept the currency because transaction fees are lower than the 2 to 3% typically imposed by credit card processors.[7] Notable vendors include WordPress, OkCupid, Reddit, and Chinese Internet giant Baidu.[8]

Speculators have been attracted to Bitcoin, fueling volatility and price swings. As of July 2013, the use of Bitcoin in the retail and commercial marketplace is relatively small compared with the use by speculators.[9]

Bitcoins are stored by associating them with addresses called "wallets". Wallets can be stored on web services, on local hardware like PCs and mobile devices, or on paper print-outs. Thefts of bitcoins from web services and online wallets have been covered in the media, prompting assertions that the safest way to store bitcoins is on secure paper wallets.
      """)
  
  def extract(text: String) {
    val resp = MetaDataExtractor.extract(text)
    print("tfIdfKeywords: ")
    resp
      .tfIdfKeywords
      .take(20)
      .foreach(keyword => print(keyword.lexeme + ":" + keyword.weight + ", "))
    println
    print("Category lexemes: ")
    resp
      .categoryLexemes
      .take(20)
      .foreach(lexeme => print(lexeme.lexeme + ":" + lexeme.weight + ", "))
    println
    resp
      .categories
      .take(5)
      .foreach(category => println(category.categoryTitle + ": " + category.weight))
  }
}