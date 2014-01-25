Wikivector
==========

The main goal of this project is to build machine learning and information retrieval tools, which use 100GB of Wikipedia data for models training.

Currently achieved goals:
* calculate tf-idf values for lexemes in Wikipedia (covered 20M lexemes)
* refine Wikipedia categories graph (it is not very clean at the moment, and contains many strange pathes)
* build classifier which can calculate relevant Wikipedia categories for given text (covered 2.5M categories)
* provide API for extraction of top td-idf terms, Wikipedia categories, and category related terms from given text
* support popular languages, currently supported: English, Russian, Arabic, Dutch, German, French, Polish, Italian, Portuguese, Spanish, Ukranian.

You can find some results of analysis bellow. Example of code in the following sections.

Output for short Scala overview article: http://www.scala-lang.org/what-is-scala.html :

    tfIdfKeywords:scala:193.77170051248623,java:52.95372539848002,function:52.181138693912224,object-ori:42.845987417422116,scalabl:31.183415781183864,framework:22.424868316076083,pattern:19.45986022975381,mutabl:19.3197022298865,immut:16.40817674230587,seamless:15.741342536279676,object:15.40676308543515,method-cal:15.008148413796308,tool:13.703691178437554,prefer:12.975715536436624,compil:12.879375818289226,oderski:12.502622476805572,support:12.214996043652862,jvms:12.160336270318938,concurr:12.153787670093097,syntax:12.091897879302746,
    Category lexemes:object-ori:151.79924643131352,jvms:129.11795272153125,oderski:85.40973022877834,compile-tim:76.69542850755704,multi-thread:64.373084658133,javaon:64.15595681501244,intellij:56.11033125437374,netbean:44.592142889980096,jvm:42.90886564603815,mutabl:19.461838661675905,future-proof:8.720668389885525,interop:8.270132065295357,scalabl:7.849034182808977,worksheet:4.978275360027274,
    Category:Programming paradigms:30.164437612688584
    Category:Programming language families:29.840150811217324
    Category:Object-oriented programming:29.808276687088345
    Category:Articles with example code:27.212404230851345
    Category:Concurrent computing:27.155771600830445
    
Stock news from Reuters: http://www.reuters.com/article/2013/11/27/us-markets-stocks-idUSBRE9AA0IH20131127 :

    tfIdfKeywords:s&p:48.97042059033133,percent:47.333692439587765,nasdaq:29.880743016112394,dow:20.576940142543165,close:19.017667564511584,market:18.862727666084133,stock:18.381896318650856,index:17.506806840992763,tech:15.843925634874989,reuters\/univers:15.701295594356253,hewlett-packard:15.463875704679813,tech-heavi:14.785004862482097,fell:14.297342434390117,sector:14.279279880380386,500:14.158308864598915,crude:13.859075413902122,27.001:13.450003795749756,ixic:13.450003795749756,expect:12.837610037289938,trader:12.83290180042393,
    Category lexemes:s&p:74.02537758920845,ixic:17.423559232422658,worst-perform:14.94930725137575,nasdaq:7.511574330006619,spni:3.1654320416021546,higher-than-expect:3.1621193273319705,
    Category:Stock exchanges:18.844571774096586
    Category:Stock market indices:18.746243430346844
    Category:Wikipedia categories named after stock market indices:18.740199037550834
    Category:Stock market indices by continent:15.844947362286838
    Category:Stock market indices by country:15.841454852224125
    
Mazda CX-5 review: http://www.caranddriver.com/reviews/2013-mazda-cx-5-first-drive-review :

    tfIdfKeywords:cx-5:223.80712290342382,mazda:188.40185346520448,suv:67.78253652297396,skyactiv:63.654405643932755,cx-7:44.761744351357606,fun-to-dr:43.345597877582655,mileag:31.831236365433508,model:27.202061512273822,deliv:24.32708804228273,front-driv:23.85706931252323,2.0-liter:23.558644516149876,engin:23.036704090278334,boomi:22.676393939135778,drive:21.53461418081624,percent:21.037196639816784,compact:20.00138356798216,six-spe:19.859708942452475,steer:19.002495514847645,rpm:18.472032654119666,mph:17.737553040612177,
    Category lexemes:cx-5:1632.646982796331,front-driv:273.4722295164951,skyactiv:273.03089753797764,all-wheel-dr:251.70274533556056,six-spe:232.4215211535967,four-cylind:203.23523696552803,2.0-liter:192.7949043076423,euro-spec:154.41612086998356,2.2-liter:153.60092641225683,boxier:145.04277484504115,fun-to-dr:133.78904025281594,cx-7:124.02374135210987,dual-zon:101.11602321421971,zoom-zoom:96.24229042353969,manual-transmiss:91.87837325244529,suv:85.33380214203868,u.s.-market:82.23441278904903,sports-car:63.95451652747622,drivelin:55.115977008624725,all-wheel:53.37148219478685,
    Category:2000s automobiles:138.50185240632828
    Category:Off-roading:132.77455878075293
    Category:Off-road vehicles:132.7332686846516
    Category:Green vehicles:117.61510079064729
    Category:SUVs:109.28925105993333
    
Russian financial news: http://lenta.ru/news/2013/11/27/basket/ :

    tfIdfKeywords:бивалютн:36.59762046576131,рубл:36.533916169510725,бирж:28.824536388479302,копеек:22.863986334661064,корзин:21.488261534394724,евр:20.97563030478338,доллар:18.76370340903501,сесс:18.169027885071266,цен:13.091795356568873,коридор:12.979598345251366,ноябр:12.15601007775221,курс:11.974408507516628,нефт:11.722585014371505,границ:11.027360709076685,93,36:10.914008577671916,максимум:10.48239449932796,средневзвешен:10.07280829715283,подорожа:9.753520885285718,38,35:9.670405978575213,паден:9.60347911863312,
    Category lexemes:копеек:23.066292913526564,средневзвешен:22.95922111823851,38,35:21.867807844420263,бирж:21.866545890250954,93,36:19.283185857800127,бивалютн:18.4894848936164,подорожа:9.806561389888843,копейк:7.52200781558836,сделк:4.6956887806298155,рассчита:4.238812609474223,укреп:3.188538209579522,нефт:2.9712190390825777,12,3:2.907651186559428,утр:2.6604598621495876,стоимост:2.5535168694114585,0,7:2.380031831221264,сдвинул:2.183461502796707,0,3:1.9181339760393645,возобнов:1.4795137359944657,валют:1.4251754257819962,
    Категория:Компании России:13.856624165118236
    Категория:Валюты:13.205488216254063
    Категория:Экономические кризисы:11.696409962756864
    Категория:Экономические рынки:11.445464116123933
    Категория:Биржи:9.841284105375928


Architecture
============

Wikivector has following main components:

* training pipeline - parses wikipedia data, calculates statistics for lexemes and categories, trains Naive Bayesian classifier for categories.
* API - jar library

Wikivector uses Scala as main programming language, because it's very expressive, fast and very convenient for writing complex algorithms where performance is critical, and Cassandra as DB backend, because it is amazingly fast and allows to achieve 100+k inserts per second on mediocre desktop grade hardware.

API: Setup
==========

Following steps have to be done in order to use Wikivector API:

* install jvm

* install Cassandra database

* create wikivector schema using cassandra-cli tool and definition in [schema file](https://github.com/crypto5/wikivector/blob/master/schema/create.cli)

* download wikivector distribution: [link](
http://dl.bintray.com/crypto5/wikivector/wikivector/wikivector/1.0.0/wikivector-1.0.0-dist.zip)

* unzip distribution file

* download model files (size is about 6GB): http://goo.gl/ZRpcXj

* unzip model file

* modify Cassandra connectivity settings in config.properties file in distribution directory

* import model files into Cassandra by running following command from distribution directory: load_db.sh [directory with unzipped model files]

How to use API + example
===========================

API is implemented as a jar library, and provides API described in [MetaDataExtractor.scala](https://github.com/crypto5/wikivector/blob/master/src/main/scala/org/wikivector/api/MetaDataExtractor.scala) file.

Following example illustrates how to use API. Example is implemented using Scala and SBT, but porting to other JVM languages is straightforward.

build.sbt file:

    name := "wikivectorclient"
    
    version := "1.0.0"
    
    scalaVersion := "2.10.3"
    
    resolvers += "Wikivector repository" at "http://dl.bintray.com/crypto5/wikivector"
    
    libraryDependencies += "wikivector" % "wikivector" % "1.0.0"
    
    // For extracting text content from web pages
    libraryDependencies += "de.l3s.boilerpipe" % "boilerpipe" % "1.1.0"
    
    libraryDependencies += "net.sourceforge.nekohtml" % "nekohtml" % "1.9.15"
    
Application which extracts text from web-pages, computes top tf-idf lexemes and relevant categories:

    import org.wikivector.api.MetaDataExtractor
    import de.l3s.boilerpipe.extractors.ArticleExtractor
    import java.net.URL
    import scala.collection.JavaConversions._
    import org.wikivector.config.CassandraConfig
    
    object Test extends App {
      CassandraConfig.seedHosts = "127.0.0.1:9160"
      extract("http://www.scala-lang.org/what-is-scala.html")
  
      def extract(url: String) {
        val text = ArticleExtractor.INSTANCE.getText(new URL(url))
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


Training pipeline
=================

Workflow of training pipeline is defined in https://github.com/crypto5/wikivector/blob/master/src/main/scala/org/wikivector/batch/Workflow.scala class.
It requires Wikipedia xml dump files which can be downloaded from http://en.wikipedia.org/wiki/Wikipedia:Database_download#Where_do_I_get... i.e. for NL wilipedia you should use http://dumps.wikimedia.org/nlwiki/latest/nlwiki-latest-pages-articles.xml.bz2 .
Recomended way to run it is to checkout source code, install SBT, and run Workflow application using SBT.
You may need to change some parameters to make it working, specifically:

define path to directory with data files (i.e. wikipedia dumps) in https://github.com/crypto5/wikivector/blob/master/src/main/scala/org/wikivector/config/GlobalConfig.scala

Cassandra config in https://github.com/crypto5/wikivector/blob/master/src/main/scala/org/wikivector/config/CassandraConfig.scala

wikipedia dump files to language mappings in https://github.com/crypto5/wikivector/blob/master/src/main/scala/org/wikivector/batch/page/LoadWikipages.scala

Training process can take few weeks, so be patient.

Happy hacking ;-)

[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/0a2c0b43ce043d6934b2a5a78325f475 "githalytics.com")](http://githalytics.com/crypto5/wikivector)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/crypto5/wikivector/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
