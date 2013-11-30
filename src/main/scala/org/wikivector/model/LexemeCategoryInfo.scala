package org.wikivector.model

import org.msgpack.annotation.Message

@Message
class LexemeCategoryInfo {
  var lexeme = ""
  var categoryId = 0
  var lexemeTitleCount = 0L
  var lexemeDocCount = 0L
  var lexemeTotalCount = 0L
  var categoryDocCount = 0L
  var categoryTotalCount = 0L
  var lexemeCategoryTitleCount = 0L
  var lexemeCategoryDocCount = 0L
  var lexemeCategoryTotalCount = 0L
}