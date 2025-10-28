package com.example.projetosaveit.api.network

import com.example.projetosaveit.adapter.recycleView.Produto
import com.example.projetosaveit.adapter.recycleView.Vitrine
import com.example.projetosaveit.model.ChatDTO
import com.example.projetosaveit.model.EmpresaDTO
import com.example.projetosaveit.model.EmpresaInsertDTO
import com.example.projetosaveit.model.EstoqueDTO
import com.example.projetosaveit.model.FuncionarioDTO
import com.example.projetosaveit.model.FuncionarioInsertDTO
import com.example.projetosaveit.model.ImagemDTO
import com.example.projetosaveit.model.LoteDTO
import com.example.projetosaveit.model.LoteInsertDTO
import com.example.projetosaveit.model.ProdutoInfoDTO
import com.example.projetosaveit.model.RelatorioDTO
import com.example.projetosaveit.model.RelatorioProdutoDTO
import com.example.projetosaveit.model.VitrineDTO
import com.example.projetosaveit.model.VitrineInsertDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

//    API de SQL

    @GET("api/stock/relatorioProdutoPorProduto/{enterpriseId}/{productId}")
    fun getRelatorioProduto(@Path("enterpriseId") enterpriseId: Long, @Path("productId") productId: Long
    ): Call<List<RelatorioProdutoDTO>>

    @GET("api/stock/relatorioProduto/{enterpriseId}")
    fun getRelatorioProdutos(@Path("enterpriseId") idEmpresa : Long): Call<List<RelatorioDTO>>

    @POST("/api/stock/inserir")
    fun postEstoque(@Body estoque : EstoqueDTO) : Call<ResponseBody>

    @GET("/api/batch/selecionarSku/{sku}")
    fun getBatchSku(@Path("sku") sku : String) : Call<LoteDTO>

    @GET("api/batch/listarProdutosLote/{enterpriseId}")
    fun getProdutos(@Path("enterpriseId") idEmpresa : Long): Call<List<Produto>>

    @GET("api/image/showcase-images/{showcaseId}")
    fun getVitrine(@Path("showcaseId") showcaseId : Long): Call<VitrineDTO>

    @GET("api/batch/informacoesProduto/{batchId}")
    fun getProdutoId(@Path("batchId") idProduto : Long): Call<ProdutoInfoDTO>

    @PATCH("api/enterprise/atualizarParcial/{id}")
    fun patchEmpresaId(@Path("id") id : Long, @Body updates: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @PATCH("/api/batch/atualizarParcial/{id}")
    fun patchProdutoId(@Path("id") id : Long, @Body updates: Map<String, @JvmSuppressWildcards Any>): Call<ResponseBody>

    @GET("api/showcase/listarVitrine/{category}")
    fun getVitrineProdutos(@Path("category") category : String): Call<List<Vitrine>>

    @GET("api/image/selecionarPorProduto/{productId}")
    fun getImagemProduto(@Path("productId") idProduto : Long): Call<ImagemDTO>

    @GET("api/showcase/selecionarPorEmpresa/{enterpriseId}")
    fun getVitrinesEmpresa(@Path("enterpriseId") idEmpresa : Long): Call<List<Vitrine>>

    @POST("api/showcase/inserir")
    fun postVitrine(@Body vitrine : VitrineInsertDTO) : Call<ResponseBody>

    @POST("api/image/inserir")
    fun postImagem(@Body imagem : ImagemDTO) : Call<ResponseBody>

    @POST("api/batch/inserir")
    fun postLote(@Body produto: LoteInsertDTO): Call<ResponseBody>

    @GET("api/enterprise/listarEmail/{email}")
    fun getEmpresaEmail(@Path("email") email : String): Call<EmpresaDTO>

    @POST("api/employee/inserir")
    fun postFuncionario(@Body funcionario: FuncionarioInsertDTO): Call<ResponseBody>

    @POST("api/enterprise/inserir")
    fun postEmpresa(@Body empresa : EmpresaInsertDTO) : Call<ResponseBody>

    @GET("api/enterprise/listarId/{id}")
    fun getEmpresaId(@Path("id") id : Long): Call<EmpresaDTO>

    @GET("api/employee/buscarPorEmail/{email}")
    fun getFuncionarioEmail(@Path("email") email : String): Call<FuncionarioDTO>

//    API de Mongo

    @GET("chats/enterprise/{enterpriseId}")
    fun getChatsEmpresa(
        @Path("enterpriseId") idEmpresa: Long
    ): Call<List<ChatDTO>>

    @GET("chats/ultimamensagem")
    fun getChatUltimaMensagem(
        @Query("chatId") idChat: Long, @Query("enterpriseId") idEmpresa: Long
    ): Call<ChatDTO>

    @GET("chats/outraempresa")
    fun getChatOutraEmpresa(
        @Query("chatId") idChat: Long, @Query("enterpriseId") idEmpresa: Long
    ): Call<ChatDTO>

    @GET("chats/buscarchatweb")
    fun getChatsHistorico(
        @Query("chatId") idChat: Long
    ): Call<List<ChatDTO>>
}