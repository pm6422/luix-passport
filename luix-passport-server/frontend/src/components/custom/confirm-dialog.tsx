import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger
} from "@/components/ui/alert-dialog"
import React from "react"

type Props = {
  children?: React.ReactNode
  title: string
  description: string
  onConfirm: () => void
  open?: boolean
  onOpenChange?: (open: boolean) => void
  cancelText?: string
  confirmText?: string
  onCancel?: () => void
}

export const ConfirmDialog = ({
  children,
  title,
  description,
  onConfirm,
  open,
  onOpenChange,
  cancelText = "Cancel",
  confirmText = "Confirm",
  onCancel
}: Props) => (
  <AlertDialog open={open} onOpenChange={onOpenChange}>
    {children && (
      <AlertDialogTrigger asChild>
        {children}
      </AlertDialogTrigger>
    )}
    <AlertDialogContent>
      <AlertDialogHeader>
        <AlertDialogTitle>{title}</AlertDialogTitle>
        <AlertDialogDescription>{description}</AlertDialogDescription>
      </AlertDialogHeader>
      <AlertDialogFooter>
        <AlertDialogCancel onClick={onCancel}>{cancelText}</AlertDialogCancel>
        <AlertDialogAction onClick={onConfirm}>{confirmText}</AlertDialogAction>
      </AlertDialogFooter>
    </AlertDialogContent>
  </AlertDialog>
)