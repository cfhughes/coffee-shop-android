package edu.cnm.deepdive.coffeeshop.repository

import java.io.IOException

class ServiceException(override val message: String): IOException(message)