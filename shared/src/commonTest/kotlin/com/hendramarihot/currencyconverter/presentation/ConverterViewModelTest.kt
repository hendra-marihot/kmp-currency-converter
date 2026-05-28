package com.hendramarihot.currencyconverter.presentation

import com.hendramarihot.currencyconverter.data.CurrencyRepository
import com.hendramarihot.currencyconverter.data.FakeCurrencyApi
import com.hendramarihot.currencyconverter.data.model.supportedCurrencies
import com.hendramarihot.currencyconverter.domain.ConvertCurrencyUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeApi = FakeCurrencyApi().apply {
        setRates("USD", mapOf("EUR" to 0.85, "IDR" to 15_000.0))
        setRates("IDR", mapOf("USD" to 0.000067))
    }
    private val repository = CurrencyRepository(fakeApi)
    private val useCase = ConvertCurrencyUseCase(repository)

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    private val testScopes = mutableListOf<CoroutineScope>()

    @AfterTest
    fun tearDown() {
        testScopes.forEach { it.cancel() }
        testScopes.clear()
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ConverterViewModel {
        val scope = CoroutineScope(testDispatcher)
        testScopes.add(scope)
        return ConverterViewModel(
            convertCurrencyUseCase = useCase,
            scope = scope,
        )
    }

    @Test
    fun initialState_hasDefaultValues() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals(supportedCurrencies, state.currencies)
        assertEquals(supportedCurrencies[0], state.fromCurrency)
        assertEquals(supportedCurrencies.first { it.code == "IDR" }, state.toCurrency)
        assertEquals("1.00", state.amount)
        assertNull(state.result)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun onFromCurrencyChanged_updatesStateAndClearsResult() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val eur = supportedCurrencies.first { it.code == "EUR" }

        viewModel.onFromCurrencyChanged(eur)

        assertEquals(eur, viewModel.uiState.value.fromCurrency)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun onToCurrencyChanged_updatesStateAndClearsResult() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val gbp = supportedCurrencies.first { it.code == "GBP" }

        viewModel.onToCurrencyChanged(gbp)

        assertEquals(gbp, viewModel.uiState.value.toCurrency)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun onAmountChanged_updatesState() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onAmountChanged("42.50")

        assertEquals("42.50", viewModel.uiState.value.amount)
    }

    @Test
    fun onSwapCurrencies_swapsFromAndTo() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        val originalFrom = viewModel.uiState.value.fromCurrency
        val originalTo = viewModel.uiState.value.toCurrency

        viewModel.onSwapCurrencies()

        assertEquals(originalTo, viewModel.uiState.value.fromCurrency)
        assertEquals(originalFrom, viewModel.uiState.value.toCurrency)
    }

    @Test
    fun onConvert_withValidAmount_setsResultAndClearsLoading() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onAmountChanged("100")

        viewModel.onConvert()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.result)
        assertEquals(100.0, state.result?.fromAmount)
    }

    @Test
    fun onConvert_whenScopeCancelled_doesNotSetErrorState() = runTest(testDispatcher) {
        fakeApi.delayMillis = 1_000
        val scope = CoroutineScope(testDispatcher)
        testScopes.add(scope)
        val viewModel = ConverterViewModel(convertCurrencyUseCase = useCase, scope = scope)
        viewModel.onAmountChanged("100")

        viewModel.onConvert()
        advanceTimeBy(10)
        scope.coroutineContext.cancelChildren()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun onConvert_withInvalidAmount_doesNothing() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onAmountChanged("abc")

        viewModel.onConvert()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertNull(state.error)
        assertEquals(0, fakeApi.callCount)
    }

    @Test
    fun onConvert_withEmptyAmount_doesNothing() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.onAmountChanged("")

        viewModel.onConvert()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.result)
    }

    @Test
    fun onConvert_withApiError_setsErrorState() = runTest(testDispatcher) {
        fakeApi.shouldThrow = RuntimeException("Network error")
        val viewModel = createViewModel()

        viewModel.onConvert()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.result)
        assertEquals("Network error", state.error)
    }

    @Test
    fun onErrorShown_clearsError() = runTest(testDispatcher) {
        fakeApi.shouldThrow = RuntimeException("Some error")
        val viewModel = createViewModel()

        viewModel.onConvert()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.onErrorShown()

        assertNull(viewModel.uiState.value.error)
    }
}
