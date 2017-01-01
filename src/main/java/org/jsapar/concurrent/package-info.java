/**
 * Can be used to add concurrency. Use {@link org.jsapar.concurrent.ConcurrentConvertTask}
 * to run the {@link org.jsapar.compose.Composer} in a different thread than the parser. Please note that by using
 * concurrency, you also introduce a lot more complexity. Concurrency is only needed when parsing really large sources
 * or when both source and target are really slow. Also note that the initialization time for these classes is higher than for the
 * single threaded versions.
 */
package org.jsapar.concurrent;