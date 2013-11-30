package org.wikivector.batch.category

import org.wikivector.dao.CategoryDao
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import org.wikivector.model.Category
import com.google.common.collect.HashMultimap
import org.wikivector.dao.WikiPageCategoryDao
import org.mapdb.DBMaker
import org.mapdb.Fun
import scala.collection.mutable
import java.util.NavigableSet
import org.mapdb.Bind
import org.wikivector.dao.CategoryDirectParentsDao
import org.wikivector.dao.CategoryAllParentsDao


object CalculateCategoriesParents extends App {
  val memdb = DBMaker.newDirectMemoryDB().writeAheadLogDisable().compressionEnable().make()
  val childsMap = memdb.getTreeSet[Fun.Tuple2[Integer, Integer]]("childs")
  val filteredChildsMap = memdb.getTreeSet[Fun.Tuple2[Integer, Integer]]("filtered_childs")
  
  run()
  
  def run() {
	var processed = 0L
	val categories = new ArrayBuffer[Category]
	// create map which maps parent categories to childs
	CategoryDao.getAllCategories.foreach{ category =>
	  categories += category
	  WikiPageCategoryDao.getParents(category.pageId).foreach{ parent =>
	    childsMap.add(Fun.t2(parent, category.id))
	  }
	  processed += 1
	  if(processed % 1000 == 0) println("Step1 %d processed".format(processed))
	}
	// find root wikipedia Categories(i.e. Category:Contents)
	val rootCategories = new mutable.ArrayBuffer[Category]
	processed = 0L
	categories.foreach{ category =>
	  if(category.pageId == "en14105005" || category.enPageId == "en14105005") rootCategories += category
	  processed += 1
	  if(processed % 1000 == 0) println("Step2 %d processed".format(processed))
	}
	println("rootCategories = " + rootCategories.map(_.title))
	println("Calculate direct parents.")
	// breadth first traverse graph and calculate parents for each category starting from root categories
	// and save them to CategoryDirectParentsCF and filteredChildsMap. Stop on level 10 to avoid loops and noisy paths.
	// Ignore links to already processed levels and links into the same level.
	var level = 0
	val visited = new mutable.HashSet[Integer]
	var current = rootCategories.map(_.id).toSet
	while(level < 11) {
	  val next = new mutable.HashSet[Integer]
	  visited ++= current
	  current.foreach{ categoryId =>
	    getChilds(categoryId, childsMap).foreach{ childCategoryId =>
	      if(!visited.contains(childCategoryId)) {
	    	// Don't save first 4 levels
	        if(level > 4) CategoryDirectParentsDao.save(childCategoryId, categoryId)
	    	filteredChildsMap.add(Fun.t2(categoryId, childCategoryId))
	    	next += childCategoryId
	      }
	    }
	  }
	  current = next.toSet
	  level += 1
	}
	println("Calculate all parents.")
	// Depth first traversal and save all parents for each category. Using filteredChildsMap for traversal.
	rootCategories.map(_.id).toSet.foreach{ categoryId: Integer => 
	  saveAllParents(categoryId, 0, Set())
	}
  }
  
  def saveAllParents(categoryId: Integer, level: Int, path: Set[Integer]) {
    val newPath = if(level > 4) path + categoryId else path
    getChilds(categoryId, filteredChildsMap).foreach{ childCategoryId =>
      if(!path.contains(childCategoryId)) {
        newPath.foreach(CategoryAllParentsDao.save(childCategoryId, _))
        saveAllParents(childCategoryId, level + 1, newPath)
      }
    }
  }
	
  def getChilds(parent: Integer, map: NavigableSet[Fun.Tuple2[Integer, Integer]]) = Bind.findSecondaryKeys(map, parent).toSet
}