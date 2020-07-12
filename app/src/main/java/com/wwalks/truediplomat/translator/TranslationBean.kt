package com.wwalks.truediplomat.translator

class TranslationBean {
    var chatName: String ? =null
    var fromLanguageName: String? = null
    var toLanguageName: String? = null
    var fromLanguageCode: String? = null
    var toLanguageCode: String? = null
    var typeOfChat: String? = null
    var date: String? = null
    var time: String? = null
    var answers: List<AnswersBean>? = null


    class AnswersBean {
        /**
         * id : weight
         * value : 98
         * units : kg
         */

        var fromText: String? = null
        var toText: String? = null
        var typeOfMessage: String? = null
    }

}
