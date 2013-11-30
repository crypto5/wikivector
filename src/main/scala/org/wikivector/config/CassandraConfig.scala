package org.wikivector.config

import com.netflix.astyanax.AstyanaxContext
import com.netflix.astyanax.Keyspace
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.connectionpool.NodeDiscoveryType
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import com.netflix.astyanax.model.ColumnFamily
import com.netflix.astyanax.serializers.StringSerializer
import com.netflix.astyanax.serializers.LongSerializer
import com.netflix.astyanax.serializers.IntegerSerializer
import com.netflix.astyanax.serializers.ByteSerializer
import com.netflix.astyanax.serializers.BytesArraySerializer
import com.netflix.astyanax.retry.ExponentialBackoff
import com.netflix.astyanax.AuthenticationCredentials
import com.netflix.astyanax.connectionpool.impl.SimpleAuthenticationCredentials

object CassandraConfig {
  lazy val Keyspace = getKeyspace("wikivector")
  lazy val WikiPageCF = new ColumnFamily("WikiPageCF", StringSerializer.get(), StringSerializer.get())
  lazy val CategoryCF = new ColumnFamily("CategoryCF", IntegerSerializer.get(), StringSerializer.get())
  lazy val CategoryCountersCF = new ColumnFamily("CategoryCountersCF", IntegerSerializer.get(), StringSerializer.get())
  lazy val CategoryDirectParentsCF = new ColumnFamily("CategoryDirectParentsCF", IntegerSerializer.get(), IntegerSerializer.get())
  lazy val CategoryAllParentsCF = new ColumnFamily("CategoryAllParentsCF", IntegerSerializer.get(), IntegerSerializer.get())
  lazy val WikiPageCategoryCF = new ColumnFamily("WikiPageCategoryCF", StringSerializer.get(), IntegerSerializer.get())
  lazy val LexemeCountersCF = new ColumnFamily("LexemeCountersCF", StringSerializer.get(), StringSerializer.get())
  lazy val LexemeCategoryInfoCF = new ColumnFamily("LexemeCategoryInfoCF", StringSerializer.get(), IntegerSerializer.get())
  lazy val LexemeCategoryTitleCountersCF = new ColumnFamily("LexemeCategoryTitleCountersCF", StringSerializer.get(), IntegerSerializer.get())
  lazy val LexemeCategoryDocCountersCF = new ColumnFamily("LexemeCategoryDocCountersCF", StringSerializer.get(), IntegerSerializer.get())
  lazy val LexemeCategoryTotalCountersCF = new ColumnFamily("LexemeCategoryTotalCountersCF", StringSerializer.get(), IntegerSerializer.get())
  lazy val ProcessedCF = new ColumnFamily("ProcessedCF", StringSerializer.get(), StringSerializer.get())
  
  var username: String = null
  var password: String = null
  var seedHosts: String = null

  def getKeyspace(keyspace: String): Keyspace = {
    val connectionPoolConfiguration = (new ConnectionPoolConfigurationImpl("ConnPool"))
    .setPort(9160)
    .setMaxConnsPerHost(20)
    .setMaxBlockedThreadsPerHost(20)
    .setSeeds("127.0.0.1:9160")
    .setConnectTimeout(10000)
    if(username != null && password != null) {
      connectionPoolConfiguration.setAuthenticationCredentials(new SimpleAuthenticationCredentials(username, password)) 
    }
    if(seedHosts != null) {
      connectionPoolConfiguration.setSeeds(seedHosts)
    }
    val context = new AstyanaxContext.Builder()
      .forCluster("cluster")
      .forKeyspace(keyspace)
      .withAstyanaxConfiguration(
        new AstyanaxConfigurationImpl()
          .setDiscoveryType(NodeDiscoveryType.NONE)
          .setRetryPolicy(new ExponentialBackoff(250, 5)))
    .withConnectionPoolConfiguration(connectionPoolConfiguration)
    .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
    .buildKeyspace(ThriftFamilyFactory.getInstance())
    context.start()
    return context.getEntity()
  }
}