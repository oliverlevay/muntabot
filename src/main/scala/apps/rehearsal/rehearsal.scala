package rehearsal

import shared._
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.window
import muntabot.Muntabot

object Rehearsal extends App:
  type Subpage = "week" | "category"
  val page = "#rehearsal"
  val title = "Instuderingsfrågor från muntabot"

  private var _currentSubpage: Subpage = "week"

  override def currentSubpage = _currentSubpage

  def setSubpage(subpage: Subpage) =
    document.location.hash = s"$page/$subpage"
    _currentSubpage = subpage

  def setupUI(): Unit =
    try {
      _currentSubpage =
        document.location.hash.split("/")(1).asInstanceOf[Subpage]
    } catch error => {}
    if (currentSubpage == "week") then perWeek()
    else if (currentSubpage == "category") then perCategory()

  def setupCommonComponents(): dom.Element =
    val containerElement = Document.setupContainer()

    Document.appendLinkToApp(containerElement, Muntabot, "Tillbaka hem")

    Document.appendText(
      containerElement,
      "h1",
      "Instuderingsfrågor från muntabot"
    )

    Document.appendButton(
      containerElement,
      "Per vecka",
      disabled = currentSubpage == "week"
    ) {
      setSubpage("week")
      perWeek()
    }

    Document.appendButton(
      containerElement,
      "Per kategori",
      disabled = currentSubpage == "category"
    ) {
      setSubpage("category")
      perCategory()
    }

    containerElement

  def perCategory(): Unit =
    val containerElement = setupCommonComponents()

    Document.appendText(containerElement, "h2", "Per kategori")

    var number = 1
    for questionType <- Questions.types do
      Document.appendText(containerElement, "h3", questionType.title)
      Document.appendText(
        containerElement,
        "p",
        s"Instruktion: ${Concepts.instruction}"
      )
      for question <- questionType.all do
        Document.appendText(
          containerElement,
          "p",
          s"$number. ${questionType.getShortQuestion(question)}"
        )
        number += 1

  def perWeek(): Unit =
    val containerElement = setupCommonComponents()
    var weeks = terms.map(_._1).distinct
    var number = 1
    for week <- weeks do
      val thisWeek = terms.filter(_._1 == week)
      val w = thisWeek(0)._1.w
      Document.appendText(
        containerElement,
        "h2",
        s"Vecka $w: ${Week.title(thisWeek(0)._1)}"
      )
      for term <- thisWeek do
        Document.appendText(containerElement, "h3", term._2.title)
        Document.appendText(
          containerElement,
          "p",
          s"Instruktion: ${Concepts.instruction}"
        )
        for question <- term._2 do
          Document.appendText(
            containerElement,
            "p",
            s"$number. ${term._2.getShortQuestion(question)}"
          )
          number += 1