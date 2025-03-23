// PlaidService.kt
package com.mrxgrc

import com.google.gson.*
import com.plaid.client.model.*
import com.plaid.client.request.PlaidApi
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object PlaidService {
    private const val CLIENT_ID = "6790d22afa5f890022fcec7b"
    private const val SECRET = "1db6959908f3e5716ed3842a513e33"

    // Custom Gson adapter for OffsetDateTime
    private val offsetDateTimeAdapter = object : JsonSerializer<OffsetDateTime>, JsonDeserializer<OffsetDateTime> {
        override fun serialize(
            src: OffsetDateTime?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
        }
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): OffsetDateTime {
            return OffsetDateTime.parse(json?.asString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }

    // Custom Gson adapter for LocalDate
    private val localDateAdapter = object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        override fun serialize(
            src: LocalDate?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src?.format(DateTimeFormatter.ISO_LOCAL_DATE))
        }
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            return LocalDate.parse(json?.asString, DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    // Gson instance configured with snake_case naming and our custom adapters.
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(OffsetDateTime::class.java, offsetDateTimeAdapter)
        .registerTypeAdapter(LocalDate::class.java, localDateAdapter)
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val plaidClient: PlaidApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sandbox.plaid.com") // Use Plaid Sandbox for testing
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        plaidClient = retrofit.create(PlaidApi::class.java)
    }

    fun createLinkToken(userId: String): String? {
        if (userId.isBlank()) {
            println("ERROR: userId is empty or null")
            return null
        }

        val user = LinkTokenCreateRequestUser().apply {
            clientUserId = userId
        }

        val request = LinkTokenCreateRequest()
            .clientId(CLIENT_ID)
            .secret(SECRET)
            .clientName("Finance Manager")
            .countryCodes(listOf(CountryCode.US))
            .language("en")
            .user(user)
            .products(listOf(Products.AUTH, Products.TRANSACTIONS))

        // Debug: Print JSON before sending using Gson.
        println("JSON Sent to Plaid: ${gson.toJson(request)}")

        return runBlocking {
            val response = plaidClient.linkTokenCreate(request).execute()
            if (response.isSuccessful) {
                println("✅ Plaid Response: ${response.body()}")
                response.body()?.linkToken
            } else {
                println("Plaid API Error: ${response.errorBody()?.string()}")
                null
            }
        }
    }

    fun exchangePublicToken(userId: Int, publicToken: String): String? {
        if (publicToken.isBlank()) {
            println("ERROR: publicToken is empty or null")
            return null
        }

        val request = ItemPublicTokenExchangeRequest()
            .publicToken(publicToken)
            .clientId(CLIENT_ID)
            .secret(SECRET)

        return runBlocking {
            val response = plaidClient.itemPublicTokenExchange(request).execute()
            if (response.isSuccessful) {
                val accessToken = response.body()?.accessToken
                if (accessToken != null) {
                    println("✅ Received Access Token: $accessToken")

                    // **Store token in DB**
                    val storedId = TokenRepository.storeAccessToken(userId, accessToken)
                    if (storedId != null) {
                        println("✅ Successfully stored access token for user $userId (Token ID: $storedId)")
                    } else {
                        println("❌ Failed to store access token for user $userId")
                    }
                }
                accessToken
            } else {
                println("❌ Plaid API Error: ${response.errorBody()?.string()}")
                null
            }
        }
    }

    fun getAccounts(accessToken: String): MutableList<AccountBase>? {
        val request = AccountsGetRequest().apply {
            this.accessToken = accessToken
            this.clientId(CLIENT_ID)
            this.secret(SECRET)
        }
        return runBlocking {
            val response = plaidClient.accountsGet(request).execute()
            if (response.isSuccessful) {
                println("✅ Accounts Response: ${response.body()}")
                response.body()?.accounts
            } else {
                println("Plaid API Error (Accounts): ${response.errorBody()?.string()}")
                null
            }
        }
    }


    fun getTransactions(accessToken: String, startDate: String, endDate: String): List<Transaction>? {
        // Build the request for transactions/get.
        val request = TransactionsGetRequest().apply {
            this.accessToken = accessToken
            this.startDate = LocalDate.parse(startDate)  // Format: "YYYY-MM-DD"
            this.endDate = LocalDate.parse(endDate)      // Format: "YYYY-MM-DD"
            this.options = TransactionsGetRequestOptions().apply {
                count = 100  // or adjust as needed
                offset = 0
            }
        }
        return runBlocking {
            val response = plaidClient.transactionsGet(request).execute()
            if (response.isSuccessful) {
                println("✅ Transactions Response: ${response.body()}")
                response.body()?.transactions  // Assuming a 'transactions' field in the response.
            } else {
                println("❌ Plaid API Error (Transactions): ${response.errorBody()?.string()}")
                null
            }
        }
    }


}
