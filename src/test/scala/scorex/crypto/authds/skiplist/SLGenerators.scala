package scorex.crypto.authds.skiplist

import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.Matchers
import scorex.crypto.authds.storage.MvStoreBlobBlobStorage

import scala.util.Random

trait SLGenerators extends Matchers {
  implicit def storage: MvStoreBlobBlobStorage

  lazy val noneEmptyBytes: Gen[Array[Byte]] = for {
    key: Array[Byte] <- Arbitrary.arbitrary[Array[Byte]] if key.length < SLElement.MaxKeySize && key.length > 1
  } yield key

  lazy val optionBytes: Gen[Option[Array[Byte]]] = for {
    bytes: Array[Byte] <- noneEmptyBytes
    op: Boolean <- Arbitrary.arbitrary[Boolean]
  } yield if (op) Some(bytes) else None

  lazy val slelementGenerator: Gen[NormalSLElement] = for {
    key: Array[Byte] <- noneEmptyBytes
    value: Array[Byte] <- Arbitrary.arbitrary[Array[Byte]]
  } yield SLElement(key, value)

  lazy val slnodeGenerator: Gen[SLNode] = for {
    el <- slelementGenerator
    rightKey: Option[Array[Byte]] <- optionBytes
    downKey: Option[Array[Byte]] <- optionBytes
    level: Int <- Arbitrary.arbitrary[Int] if level >= 0
    isTower: Boolean <- Arbitrary.arbitrary[Boolean]
  } yield SLNode(el, rightKey, downKey, level, isTower: Boolean)

  def genEl(howMany: Int = 1, seed: Option[Int] = None): Seq[NormalSLElement] = {
    val r = new Random
    seed.foreach(s => r.setSeed(s))
    (1 to howMany) map (i => SLElement(r.nextString(32).getBytes, r.nextString(32).getBytes))
  }

  def updatedElement(e: NormalSLElement): NormalSLElement = {
    val newE = e.copy(value = (1: Byte) +: e.value)

    e.key shouldEqual newE.key
    e.value should not equal newE.value
    newE

  }

}
