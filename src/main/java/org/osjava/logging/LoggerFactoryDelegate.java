/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package org.osjava.logging;

interface LoggerFactoryDelegate {

	Logger createLogger(Class<?> clazz);

}
