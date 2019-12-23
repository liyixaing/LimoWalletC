package com.xms.limowallet.activity;

import org.web3j.crypto.LinuxSecureRandom;

import java.security.SecureRandom;

/**
 * Utility class for working with SecureRandom implementation.
 * <p>
 * <p>This is to address issues with SecureRandom on Android. For more information, refer to the
 * following <a href="https://github.com/web3j/web3j/issues/146">issue</a>.
 */
final class SecureRandomUtils
{

  private static final SecureRandom SECURE_RANDOM;

  static
  {
    if (isAndroidRuntime())
    {
      new LinuxSecureRandom();
    }
    SECURE_RANDOM = new SecureRandom();
  }

  static SecureRandom secureRandom()
  {
    return SECURE_RANDOM;
  }

  // Taken from BitcoinJ implementation
  private static int isAndroid = -1;

  static boolean isAndroidRuntime()
  {
    if (isAndroid == -1)
    {
      final String runtime = System.getProperty("java.runtime.name");
      isAndroid = (runtime != null && runtime.equals("Android Runtime")) ? 1 : 0;
    }
    return isAndroid == 1;
  }

  private SecureRandomUtils()
  {
  }
}
