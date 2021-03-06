package org.jetbrains.plugins.scala
package worksheet

import com.intellij.ide.scratch.{ScratchFileService, ScratchRootType}
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.ContainerUtil
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.plugins.scala.settings.ScalaProjectSettings

//noinspection TypeAnnotation
object WorksheetFileType extends LanguageFileTypeBase(WorksheetLanguage.INSTANCE) {

  override def getDefaultExtension = "sc"

  // TODO worksheet logo
  override def getIcon = ScalaFileType.INSTANCE.getIcon

  def isWorksheetFile(file: VirtualFile)
                     (flag: Project => Boolean = treatScratchFileAsWorksheet(_))
                     (implicit project: Project): Boolean =
    isMyFileType(file) && !isAmmoniteEnabled(file) ||
      hasScratchRootType(file) && flag(project)

  def isAmmoniteEnabled(file: VirtualFile)
                       (implicit project: Project): Boolean = {
    import ScalaProjectSettings.ScFileMode._
    projectSettings.getScFileMode match {
      case Worksheet => false
      case Ammonite => true
      case _ =>
        ProjectRootManager.getInstance(project).getFileIndex.isUnderSourceRootOfType(
          file,
          ContainerUtil.newHashSet(JavaSourceRootType.TEST_SOURCE)
        )
    }
  }

  def hasScratchRootType(file: VirtualFile): Boolean =
    ScratchFileService.getInstance().getRootType(file).isInstanceOf[ScratchRootType]

  def treatScratchFileAsWorksheet(implicit project: Project): Boolean =
    projectSettings.isTreatScratchFilesAsWorksheet

  private def projectSettings(implicit project: Project) =
    ScalaProjectSettings.getInstance(project)
}
