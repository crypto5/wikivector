package org.wikivector.batch

import org.wikivector.batch.page.LoadWikipages
import org.wikivector.batch.lexemes.CalculateLexemeCounters
import org.wikivector.batch.lexemes.DeleteRareLexemes
import org.wikivector.batch.page.TranslateWikiPages
import org.wikivector.batch.category.CreateWikiPageCategoryGraph
import org.wikivector.batch.category.CreateCategories
import org.wikivector.batch.category.CalculateCategoriesParents
import org.wikivector.batch.category.CalculateLexemeCategoryCounters
import org.wikivector.batch.category.CalculateCategoryCounters
import org.wikivector.batch.category.CalculateLexemeCategoryInfo

object Workflow extends App {
	// Loads wikipages from xml files to Cassandra
	LoadWikipages.run
	
	// Associates english wikipages with pages in other languages
	// It is not used at the moment, and is disabled to simplify process
	// TranslateWikiPages.run
	
	// Calculates counters for lexemes in wikipages, i.e. number of documents with given lexeme. 
	CalculateLexemeCounters.run
	
	// Deletes rarely used lexemes to make data volume smaller.
	DeleteRareLexemes.run
	
	// Loads categories from xml files to cassandra
	CreateCategories.run
	
	// Creates wikipage category graph and saves it to Cassandra.
	CreateWikiPageCategoryGraph.run
	
	CalculateCategoriesParents.run
	
	// Calculates counters for categories, i.e. number of documents within each category.
	CalculateCategoryCounters.run
	
	// Calculates counters of how many times each lexeme occurs in each category.
	CalculateLexemeCategoryCounters.run
	
	// Combine all statistics for categories and lexemes into aggregated MessagePack message.
	// It allows to make analysis process much faster.
	CalculateLexemeCategoryInfo.run
}
