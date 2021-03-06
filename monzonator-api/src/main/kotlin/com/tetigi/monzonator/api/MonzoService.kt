package com.tetigi.monzonator.api

import com.palantir.tokens.auth.AuthHeader
import com.tetigi.monzonator.api.data.AccountBalance
import com.tetigi.monzonator.api.requests.*
import com.tetigi.monzonator.api.responses.*
import retrofit2.Call
import retrofit2.http.*

interface MonzoService {
    /**
     * Returns a list of accounts owned by the currently authorised user.
     */
    @GET("accounts")
    fun getAccounts(@Header("Authorization") authHeader: AuthHeader): Call<GetAccountsResponse>

    /**
     * Returns balance information for a specific account.
     */
    @GET("balance")
    fun getBalance(
            @Header("Authorization") authHeader: AuthHeader,
            @Query("account_id") accountId: String
    ): Call<AccountBalance>

    /**
     * Returns an individual transaction, fetched by its id.
     */
    @GET("transactions/{transactionId}?expand[]=merchant")
    fun getTransaction(
            @Header("Authorization") authHeader: AuthHeader,
            @Path("transactionId") transactionId: String
    ): Call<GetTransactionResponse>

    /**
     * Returns a list of transactions on the user’s account.
     */
    @GET("transactions?expand[]=merchant")
    fun getTransactions(
            @Header("Authorization") authHeader: AuthHeader,
            @Query("account_id") accountId: String
    ): Call<GetTransactionsResponse>

    /* Some sort of patch thing TODO
    /**
     * You may store your own key-value annotations against a transaction in its metadata.
     */
    @POST
    @Path("transactions/{transactionId}")
    fun annotateTransaction(
            @Header("Authorization") authHeader: AuthHeader,
            transactionId: String
    ): Transaction
    */

    /**
     * Creates a new feed item on the user’s feed. These can be dismissed.
     */
    @POST("/feed")
    @FormUrlEncoded
    fun createFeedItem(
            @Header("Authorization") authHeader: AuthHeader,
            @FieldMap request: CreateFeedItemRequest
    ): Call<Empty>

    /**
     * Each time a matching event occurs, we will make a POST call to the URI you provide.
     * If the call fails, we will retry up to a maximum of 5 attempts, with exponential backoff.
     */
    @POST("/webhooks")
    @FormUrlEncoded
    fun registerWebhook(
            @Header("Authorization") authHeader: AuthHeader,
            @FieldMap request: RegisterWebhookRequest
    ): Call<RegisterWebhookResponse>

    /**
     * List the webhooks your application has registered on an account.
     */
    @GET("/webhooks")
    fun getWebhooks(
            @Header("Authorization") authHeader: AuthHeader,
            @Query("account_id") accountId: String
    ): Call<ListWebhooksResponse>

    /**
     * When you delete a webhook, we will no longer send notifications to it.
     */
    @DELETE("/webhooks/{webhookId}")
    fun deleteWebhook(
            @Header("Authorization") authHeader: AuthHeader,
            @Path("webhookId") webhookId: String
    ): Call<Empty>

    /**
     * The first step when uploading an attachment is to obtain a temporary URI to which the file can be uploaded.
     * The response will include a file_url which will be the URI of the resulting file,
     * and an upload_url to which the file should be uploaded to.
     */
    @POST("/attachment/upload")
    @FormUrlEncoded
    fun uploadAttachment(
            @Header("Authorization") authHeader: AuthHeader,
            @FieldMap request: UploadAttachmentRequest
    ): Call<UploadAttachmentResponse>

    /**
     * Once you have obtained a URI for an attachment, either by uploading to the upload_url obtained from the
     * upload endpoint above or by hosting a remote image, this URI can then be registered against a transaction.
     * Once an attachment is registered against a transaction this will be displayed on the detail page of
     * a transaction within the Monzo app.
     */
    @POST("/attachment/register")
    @FormUrlEncoded
    fun registerAttachment(
            @Header("Authorization") authHeader: AuthHeader,
            @FieldMap request: RegisterAttachmentRequest
    ): Call<RegisterAttachmentResponse>

    /**
     * To remove an attachment, simply deregister this using its id
     */
    @POST("/attachment/deregister")
    @FormUrlEncoded
    fun deregisterAttachment(
            @Header("Authorization") authHeader: AuthHeader,
            @FieldMap request: DeregisterAttachmentRequest
    ): Call<Empty>

    companion object {
    }
}