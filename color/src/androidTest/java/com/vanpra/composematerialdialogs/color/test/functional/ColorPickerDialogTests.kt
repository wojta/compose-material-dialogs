package com.vanpra.composematerialdialogs.color.test.functional

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.test.utils.DialogWithContent
import com.vanpra.composematerialdialogs.test.utils.assertDialogDoesNotExist
import com.vanpra.composematerialdialogs.test.utils.onDialogColorSelector
import com.vanpra.composematerialdialogs.test.utils.onDialogSubColorBackButton
import com.vanpra.composematerialdialogs.test.utils.onDialogSubColorSelector
import com.vanpra.composematerialdialogs.test.utils.onPositiveButton
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorPickerDialogTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun MaterialDialog.defaultButtons() {
        buttons {
            negativeButton("Disagree")
            positiveButton("Agree")
        }
    }

    @Test
    fun colorPickerDialogWaitForPositiveButton() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary, waitForPositiveButton = true) {
                    selectedColor = it
                }
                defaultButtons()
            }
        }

        composeTestRule.onDialogColorSelector(2).performClick()
        assertEquals(null, selectedColor)
        composeTestRule.onPositiveButton().performClick()
        /* Need this line or else tests don't wait for dialog to close */
        composeTestRule.assertDialogDoesNotExist()
        assertEquals(ColorPalette.Primary[2], selectedColor)
    }

    @Test
    fun colorPickerDialogDontWaitForPositiveButton() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary, waitForPositiveButton = false) {
                    selectedColor = it
                }
                defaultButtons()
            }
        }

        composeTestRule.onDialogColorSelector(2).performClick()
        composeTestRule.waitForIdle()
        assertEquals(ColorPalette.Primary[2], selectedColor)
        selectedColor = null
        composeTestRule.onPositiveButton().performClick()
        /* Need this line or else tests don't wait for dialog to close */
        composeTestRule.assertDialogDoesNotExist()
        assertEquals(null, selectedColor)
    }

    @Test
    fun checkSubColorBackButtonGoesBackToMainColorPage() {
        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(
                    colors = ColorPalette.Primary,
                    subColors = ColorPalette.PrimarySub,
                    waitForPositiveButton = false
                )
            }
        }

        composeTestRule.onDialogColorSelector(0).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onDialogSubColorBackButton().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onDialogSubColorBackButton().assertDoesNotExist()
        composeTestRule.onDialogColorSelector(0).assertExists()
    }

    @Test
    fun checkMainColorsSelectable() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(colors = ColorPalette.Primary, waitForPositiveButton = false) {
                    selectedColor = it
                }
            }
        }

        ColorPalette.Primary.forEachIndexed { index, color ->
            composeTestRule.onDialogColorSelector(index).performClick()
            composeTestRule.waitForIdle()
            assertEquals(color, selectedColor)
        }
    }

    @Test
    fun checkSubColorsSelectable() {
        var selectedColor: Color? = null

        composeTestRule.setContent {
            DialogWithContent {
                colorChooser(
                    colors = ColorPalette.Primary,
                    subColors = ColorPalette.PrimarySub,
                    waitForPositiveButton = false
                ) {
                    selectedColor = it
                }
            }
        }

        ColorPalette.Primary.forEachIndexed { index, _ ->
            composeTestRule.onDialogColorSelector(index).performClick()
            ColorPalette.PrimarySub[index].forEachIndexed { subIndex, subColor ->
                composeTestRule.onDialogSubColorSelector(subIndex).performClick()
                composeTestRule.waitForIdle()
                assertEquals(subColor, selectedColor)
            }
            composeTestRule.onDialogSubColorBackButton().performClick()
            composeTestRule.waitForIdle()
        }
    }
}
