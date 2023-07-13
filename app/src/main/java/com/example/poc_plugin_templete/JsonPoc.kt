package com.example.poc_plugin_templete

import wu.seal.jsontokotlin.interceptor.InterceptorManager
import wu.seal.jsontokotlin.library.JsonToKotlinBuilder
import wu.seal.jsontokotlin.model.DefaultValueStrategy
import wu.seal.jsontokotlin.model.PropertyTypeStrategy
import wu.seal.jsontokotlin.model.classscodestruct.DataClass
import wu.seal.jsontokotlin.utils.ClassImportDeclaration
import wu.seal.jsontokotlin.utils.KotlinClassCodeMaker
import wu.seal.jsontokotlin.utils.KotlinClassMaker

class JsonPoc {

    fun JsonBuildTest(input: String, className: String, isEntity: Boolean): String {
        JsonToKotlinBuilder().apply {
            setCustomAnnotation(
            "import kotlinx.serialization.SerialName\n",
            "",
            "@SerialName(\"%s\")"
            )
            setPropertyTypeStrategy(PropertyTypeStrategy.Nullable)
            setDefaultValueStrategy(DefaultValueStrategy.AllowNull)
        }
        return buildTest(input, className, isEntity)
    }

    private fun buildTest(input: String,
                          className: String,
                          isEntity: Boolean = false
    ): String {

        val imports = ClassImportDeclaration.applyImportClassDeclarationInterceptors(
            InterceptorManager.getEnabledImportClassDeclarationInterceptors()
        )

        val kotlinClass = KotlinClassMaker(
            className,
            input
        ).makeKotlinClass()

        val result = if (kotlinClass is DataClass && isEntity) {
            kotlinClass.copy(parentClassTemplate = "BaseEntity()")
        } else {
            kotlinClass
        }

        val classCode = KotlinClassCodeMaker(
            result
        ).makeKotlinClassCode()

        val importsAndClassCode = if (imports.isNotBlank()) {
            "$imports\n\n$classCode"
        } else {
            classCode
        }

        return importsAndClassCode

    }
}