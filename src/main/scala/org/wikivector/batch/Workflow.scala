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
	LoadWikipages.run
	TranslateWikiPages.run
	CalculateLexemeCounters.run
	DeleteRareLexemes.run
	CreateCategories.run
	CreateWikiPageCategoryGraph.run
	CalculateCategoriesParents.run
	CalculateCategoryCounters.run
	CalculateLexemeCategoryCounters.run
	CalculateLexemeCategoryInfo.run
}