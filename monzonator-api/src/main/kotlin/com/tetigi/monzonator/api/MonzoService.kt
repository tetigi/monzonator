package com.tetigi.monzonator.api

import com.palantir.tokens.auth.AuthHeader
import com.tetigi.monzonator.api.data.AccountBalance
import com.tetigi.monzonator.api.requests.*
import com.tetigi.monzonator.api.responses.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface MonzoService {
    /**
     * Returns a list of accounts owned by the currently authorised user.
     */
    @GET
    @Path("/accounts")
    fun getAccounts(@HeaderParam("Authorization") authHeader: AuthHeader): GetAccountsResponse

    /**
     * Returns balance information for a specific account.
     */
    @GET
    @Path("/balance")
    fun getBalance(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            @QueryParam("account_id") accountId: String
    ): AccountBalance

    /**
     * Returns an individual transaction, fetched by its id.
     */
    @GET
    @Path("/transactions/{transactionId}")
    fun getTransaction(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            transactionId: String
    ): GetTransactionResponse

    /**
     * Returns a list of transactions on the user’s account.
     */
    @GET
    @Path("/transactions")
    fun getTransactions(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            @QueryParam("account_id") accountId: String
    ): GetTransactionsResponse

    /* Some sort of patch thing TODO
    /**
     * You may store your own key-value annotations against a transaction in its metadata.
     */
    @POST
    @Path("transactions/{transactionId}")
    fun annotateTransaction(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            transactionId: String
    ): Transaction
    */

    /**
     * Creates a new feed item on the user’s feed. These can be dismissed.
     */
    @POST
    @Path("/feed")
    fun createFeedItem(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            @QueryParam("account_id") accountId: String,
            request: CreateFeedItemRequest
    )

    /**
     * Each time a matching event occurs, we will make a POST call to the URL you provide.
     * If the call fails, we will retry up to a maximum of 5 attempts, with exponential backoff.
     */
    @POST
    @Path("/webhooks")
    fun registerWebhook(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            request: RegisterWebhookRequest
    ): RegisterWebhookResponse

    /**
     * List the webhooks your application has registered on an account.
     */
    @GET
    @Path("/webhooks")
    fun getWebhooks(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            @QueryParam("account_id") accountId: String
    ): ListWebhooksResponse

    /**
     * When you delete a webhook, we will no longer send notifications to it.
     */
    @DELETE
    @Path("/webhooks/{webhookId}")
    fun deleteWebhook(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            webhookId: String
    )

    /**
     * The first step when uploading an attachment is to obtain a temporary URL to which the file can be uploaded.
     * The response will include a file_url which will be the URL of the resulting file,
     * and an upload_url to which the file should be uploaded to.
     */
    @POST
    @Path("/attachment/upload")
    fun uploadAttachment(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            request: UploadAttachmentRequest
    ): UploadAttachmentResponse

    /**
     * Once you have obtained a URL for an attachment, either by uploading to the upload_url obtained from the
     * upload endpoint above or by hosting a remote image, this URL can then be registered against a transaction.
     * Once an attachment is registered against a transaction this will be displayed on the detail page of
     * a transaction within the Monzo app.
     */
    @POST
    @Path("/attachment/register")
    fun registerAttachment(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            request: RegisterAttachmentRequest
    ): RegisterAttachmentResponse

    /**
     * To remove an attachment, simply deregister this using its id
     */
    @POST
    @Path("/attachment/deregister")
    fun deregisterAttachment(
            @HeaderParam("Authorization") authHeader: AuthHeader,
            request: DeregisterAttachmentRequest
    )
}