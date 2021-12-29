/**
 * The canonical data model used by this library.
 * <p>
 * A {@link org.jsapar.model.Document} contains multiple {@link org.jsapar.model.Line} objects where each line corresponds to a line of
 * the input buffer. Within each {@link org.jsapar.model.Line} there are multiple {@link org.jsapar.model.Cell} objects that
 * correspond to fields or items on each line within the input.
 * <p>
 * You can use this model to build a structure that you later compose to an output by using a {@link org.jsapar.compose.Composer}.
 * <p>
 * While using any of the parsers such as {@link org.jsapar.TextParser}, this model will be the result. The model is also
 * used as intermediate result in all the converter implementations.
 *
 * In order to make it easier to retrieve and alter cell values within a {@link org.jsapar.model.Line}, you may use the {@link org.jsapar.model.LineUtils} class.
 */
package org.jsapar.model;