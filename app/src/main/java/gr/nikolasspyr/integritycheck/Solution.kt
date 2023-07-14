package gr.nikolasspyr.integritycheck

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import wu.seal.jsontokotlin.library.JsonToKotlinBuilder
import wu.seal.jsontokotlin.model.DefaultValueStrategy
import wu.seal.jsontokotlin.model.PropertyTypeStrategy
import wu.seal.jsontokotlin.model.TargetJsonConverter

fun main() {
    val jsonString = """
        {
            "name": "John Doe",
            "age": 30,
            "email": "john.doe@example.com",
            "address": {
                "street": "123 Main St",
                "city": "New York",
                "country": "USA"
            },
            "friends": [
                {
                    "name": "Alice",
                    "age": 28
                },
                {
                    "name": "Bob",
                    "age": 32
                }
            ]
        }
    """
    val json1 = """{ "programmers": [
                { "isFirstName": "Brett", "lastName":"McLaughlin", "email": "aaaa" },
                { "firstName": "Jason", "lastName":"Hunter", "email": "bbbb" },
                { "firstName": "Elliotte", "lastName":"Harold", "email": "cccc" }
                ],
                "authors": [
                { "firstName": null, "lastName": "Asimov", "genre": "science fiction" },
                { "firstName": "Tad", "lastName": "Williams", "genre": "fantasy" },
                { "firstName": "Frank", "lastName": "Peretti", "genre": "christian fiction" }
                ],
                "musicians": [
                { "firstName": "Eric", "lastName": "Clapton", "instrument": "guitar" },
                { "firstName": "Sergei", "lastName": "Rachmaninoff", "instrument": "piano" }
                ] } """


//    val kotlinClass = generateSerializationDataClass(jsonString, "Person")
//    println(kotlinClass)

    val actualOutput = JsonToKotlinBuilder()
        .setPackageName("com.my.package.name")
        .enableVarProperties(false) // optional, default : false
        .setPropertyTypeStrategy(PropertyTypeStrategy.AutoDeterMineNullableOrNot) // optional, default :  PropertyTypeStrategy.NotNullable
        .setDefaultValueStrategy(DefaultValueStrategy.AvoidNull) // optional, default : DefaultValueStrategy.AvoidNull
        .setAnnotationLib(TargetJsonConverter.MoshiCodeGen) // optional, default: TargetJsonConverter.None
        .enableComments(true) // optional, default : false
        .enableOrderByAlphabetic(true) // optional : default : false
        .enableInnerClassModel(true) // optional, default : false
        .enableMapType(true)

        .enableCreateAnnotationOnlyWhenNeeded(true) // optional, default : false
        .setIndent(4)// optional, default : 4
        .enableAnnotationAndPropertyInSameLine(true) // optional, default : false
        .enableForceInitDefaultValueWithOriginJsonValue(true) // optional, default : false
        .enableForcePrimitiveTypeNonNullable(true) // optional, default : false
        .build(json1, "GlossResponse") // finally, get KotlinClassCode string

//    val output = JsonToKotlinBuilder()
//        .build(jsonString, "Person")

    val output = JsonToKotlinBuilder()
        .setCustomAnnotation(
            "import kotlinx.serialization.SerialName\n",
            "",
            "@SerialName(\"%s\")"
        )
        .setPropertyTypeStrategy(PropertyTypeStrategy.Nullable)
        .setDefaultValueStrategy(DefaultValueStrategy.AllowNull)
        .build(jsonString, "Person")

//    val outputDisplay = JsonToKotlinBuilder()
//        .enableParcelableSupport(true)
//        .build(jsonString, "Person")

    val json = "{\n" +
            "    \"glossary\":{\n" +
            "        \"title\":\"example glossary\",\n" +
            "        \"GlossDiv\":{\n" +
            "            \"title\":\"S\",\n" +
            "            \"GlossList\":{\n" +
            "                \"GlossEntry\":{\n" +
            "                    \"ID\":\"SGML\",\n" +
            "                    \"SortAs\":\"SGML\",\n" +
            "                    \"GlossTerm\":\"Standard Generalized Markup Language\",\n" +
            "                    \"Acronym\":\"SGML\",\n" +
            "                    \"Abbrev\":\"ISO 8879:1986\",\n" +
            "                    \"GlossDef\":{\n" +
            "                        \"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
            "                        \"GlossSeeAlso\":[\n" +
            "                            \"GML\",\n" +
            "                            \"XML\"\n" +
            "                        ]\n" +
            "                    },\n" +
            "                    \"GlossSee\":\"markup\"\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}"

    val outputDisplay = JsonToKotlinBuilder()
        .enableParcelableSupport(true)
        .build(json, "Person")

    val modifyEntity = JsonPoc().JsonBuildTest(jsonString, "PersonTest", true)

    println(modifyEntity)
}

//fun generateKotlinDataClass(jsonString: String, className: String): String {
//    val json = Json.decodeFromString<Map<String, Any>>(jsonString)
//    val properties = json.entries.map { (key, value) ->
//        val propertyType = when (value) {
//            is String -> "String"
//            is Boolean -> "Boolean"
//            is Int -> "Int"
//            is Double -> "Double"
//            is List<*> -> {
//                if (value.isNotEmpty() && value[0] is Map<*, *>) {
//                    val nestedClassName = "${className}_${key.capitalize()}"
//                    generateKotlinDataClass(Json.encodeToString(value), nestedClassName)
//                    "List<$nestedClassName>"
//                } else {
//                    val listElementType = value.firstOrNull()?.let {
//                        it::class.simpleName ?: "Any"
//                    } ?: "Any"
//                    "List<$listElementType>"
//                }
//            }
//            is Map<*, *> -> {
//                val nestedClassName = "${className}_${key.capitalize()}"
//                generateKotlinDataClass(Json.encodeToString(value), nestedClassName)
//                nestedClassName
//            }
//            else -> "Any"
//        }
//        "val $key: $propertyType"
//    }
//    return """
//        |@Serializable
//        |data class $className(
//        |    ${properties.joinToString(",\n    ")}
//        |)
//        |""".trimMargin()
//}

fun generateSerializationDataClass(jsonString: String, className: String): String {
    val json = Json.parseToJsonElement(jsonString)
    return generateSerializationDataClass(json, className)
}

private fun generateSerializationDataClass(jsonElement: JsonElement, className: String): String {
    return when (jsonElement) {
        is JsonPrimitive -> getPrimitivePropertyType(jsonElement)
        is JsonObject -> {
            val properties = jsonElement.entries.map { (key, value) ->
                val propertyType = generateSerializationDataClass(value, className)
                "val $key: $propertyType"
            }
            """
            @Serializable
            data class $className(
                ${properties.joinToString(",\n    ")}
            )
            """.trimIndent()
        }
        else -> throw IllegalArgumentException("Unsupported JSON structure")
    }
}

private fun getPrimitivePropertyType(jsonPrimitive: JsonPrimitive): String {
    return when {
        jsonPrimitive.isString -> "String"
        jsonPrimitive.booleanOrNull != null -> "Boolean"
        jsonPrimitive.intOrNull != null -> "Int"
        jsonPrimitive.longOrNull != null -> "Long"
        jsonPrimitive.floatOrNull != null -> "Float"
        jsonPrimitive.doubleOrNull != null -> "Double"
        else -> "Any"
    }
}