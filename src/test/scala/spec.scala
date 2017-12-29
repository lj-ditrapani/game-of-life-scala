package info.ditrapani.gameoflife

import org.scalatest.{AsyncFunSpec, FunSpec, Matchers}

abstract class Spec extends FunSpec with Matchers

abstract class AsyncSpec extends AsyncFunSpec with Matchers
