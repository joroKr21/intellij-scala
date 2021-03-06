package org.jetbrains.plugins.scala.util

import java.awt.Event
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import com.intellij.concurrency.JobScheduler
import com.intellij.diagnostic.PerformanceWatcher
import com.intellij.ide.{ApplicationInitializedListener, IdeEventQueue}
import com.intellij.openapi.application.{ApplicationManager, ModalityState, TransactionGuard}
import com.intellij.openapi.progress._
import org.jetbrains.plugins.scala.extensions._
import org.jetbrains.plugins.scala.util.UIFreezingGuard._

import scala.util.control.NoStackTrace

/**
  * @author Nikolay.Tropin
  */
class UIFreezingGuard extends ApplicationInitializedListener {

  private val periodMs = 10

  override def componentsInitialized(): Unit = {
    if (pceEnabled) {
      JobScheduler.getScheduler.scheduleWithFixedDelay(cancelOnUserInput(), periodMs, periodMs, TimeUnit.MILLISECONDS)
    }
  }

  private def cancelOnUserInput(): Unit = {
    val timestamp = progress.timestamp
    if (progress.isRunning && hasPendingUserInput) {
      progress.cancel(timestamp)
    }
  }
}

object UIFreezingGuard {

  private val pceEnabled = System.getProperty("idea.ProcessCanceledException") != "disabled"

  private var isGuarded: Boolean = false

  private def isEdt: Boolean = ApplicationManager.getApplication.isDispatchThread

  //used in macro!
  def withResponsibleUI[T](body: => T): T = {
    if (!isAlreadyGuarded && pceEnabled) {
      val start = System.currentTimeMillis()
      try {
        isGuarded = true
        val progressManager = ProgressManager.getInstance()

        if (canInterrupt) {

          if (hasPendingUserInput)
            throw UnfreezeException

          progressManager.runProcess(body, progress)
        }
        else
          body
      } finally {
        isGuarded = false
        dumpThreads(System.currentTimeMillis() - start)
      }
    }
    else body
  }

  //body should have withResponsibleUI call inside
  def withDefaultValue[T](default: T)(body: T): T = {
    if (isEdt && hasPendingUserInput) default
    else {
      try body
      catch {
        case UnfreezeException => default
      }
    }
  }

  //used in macro to reduce number of `withResponsibleUI` calls in the stacktrace
  def isAlreadyGuarded: Boolean = isEdt && isGuarded || !isEdt

  private def isWriteAction: Boolean = ApplicationManager.getApplication.isWriteAccessAllowed
  private def isTransaction: Boolean = TransactionGuard.getInstance().getContextTransaction != null

  private def isUnderProgress: Boolean = {
    val indicator = ProgressManager.getInstance().getProgressIndicator
    indicator != progress
  }
  private def hasModalityState: Boolean = ModalityState.current() != ModalityState.NON_MODAL

  private def canInterrupt: Boolean = !isWriteAction && !isTransaction && !isUnderProgress && !hasModalityState

  private def dumpThreads(ms: Long): Unit = {
    val threshold = 1000
    if (ms > threshold) {
      PerformanceWatcher.getInstance().dumpThreads("scalaEdtFreezing/", false)
    }
  }

  private def hasPendingUserInput: Boolean = {
    val queue = IdeEventQueue.getInstance()
    val userEventIds = Seq(Event.KEY_ACTION, Event.KEY_PRESS, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_WHEEL)

    userEventIds.exists(queue.peekEvent(_) != null)
  }

  private object progress extends StandardProgressIndicator {
    val delegate = new EmptyProgressIndicator()

    private val counter = new AtomicLong()

    def timestamp: Long = counter.get()

    def start(): Unit = {
      counter.incrementAndGet()
      delegate.start()
    }

    def cancel(l: Long): Unit = {
      if (timestamp == l) delegate.cancel()
    }

    def checkCanceled(): Unit = {
      if (isCanceled && canInterrupt)
        throw UnfreezeException
    }

    //EmptyProgressIndicator is good enough, but it has final `checkCanceled()` method
    def cancel(): Unit                                                 = delegate.cancel()
    def isRunning: Boolean                                             = delegate.isRunning
    def pushState(): Unit                                              = delegate.pushState()
    def setIndeterminate(indeterminate: Boolean): Unit                 = delegate.setIndeterminate(indeterminate)
    def setModalityProgress(modalityProgress: ProgressIndicator): Unit = delegate.setModalityProgress(modalityProgress)
    def isCanceled: Boolean                                            = delegate.isCanceled
    def isIndeterminate: Boolean                                       = delegate.isIndeterminate
    def isModal: Boolean                                               = delegate.isModal
    def setFraction(fraction: Double): Unit                            = delegate.setFraction(fraction)
    def stop(): Unit                                                   = delegate.stop()
    def getText: String                                                = delegate.getText
    def setText(text: String): Unit                                    = delegate.setText(text)
    def isPopupWasShown: Boolean                                       = delegate.isPopupWasShown
    def setText2(text: String): Unit                                   = delegate.setText2(text)
    def getModalityState: ModalityState                                = delegate.getModalityState
    def getFraction: Double                                            = delegate.getFraction
    def popState(): Unit                                               = delegate.popState()
    def getText2: String                                               = delegate.getText2
    def isShowing: Boolean                                             = delegate.isShowing
  }

  private object UnfreezeException extends ProcessCanceledException with NoStackTrace {
    override def getMessage: String = "Long scala calculation on UI thread canceled"
  }
}