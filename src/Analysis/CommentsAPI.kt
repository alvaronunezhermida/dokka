package org.jetbrains.dokka

import org.jetbrains.jet.lang.descriptors.*
import org.jetbrains.jet.lang.resolve.*
import org.jetbrains.jet.kdoc.psi.api.*
import org.jetbrains.jet.lang.psi.*

fun BindingContext.getDocumentation(descriptor: DeclarationDescriptor): KDoc? {
    val psiElement = DescriptorToSourceUtils.descriptorToDeclaration(descriptor)
    if (psiElement == null)
        throw IllegalArgumentException("$descriptor doesn't have connection to source code, is it synthetic?")

    return psiElement.previousSiblings().takeWhile { it !is JetDeclaration }.firstOrNull { it is KDoc } as KDoc?
}

fun KDoc?.extractText(): String {
    if (this == null)
        return ""
    val text = getText()
    if (text == null)
        return ""
    val lines = text.replace("\r", "").split("\n")
    return lines.map {
        it.dropWhile { java.lang.Character.isWhitespace(it) }
                .dropWhile { it == '/' }
                .dropWhile { it == '*' }
                .dropWhile { it == '/' }
                .dropWhile { java.lang.Character.isWhitespace(it) }
    }.filter { it.any() }.join("\n")
}