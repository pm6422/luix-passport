import { CheckCircle2 } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { Separator } from "@/components/ui/separator.tsx"
import { Link } from "react-router-dom"
import { useEffect, useState } from "react"
import { motion } from "framer-motion"

interface Props {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ContactSuccessAlertDialog({ open, onOpenChange }: Props) {
  const [isAnimating, setIsAnimating] = useState(false)

  useEffect(() => {
    if (open) {
      setIsAnimating(true)
      const timer = setTimeout(() => setIsAnimating(false), 1000)
      return () => clearTimeout(timer)
    }
  }, [open])
  
  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent className="rounded-2xl sm:max-w-[425px]">
        <AlertDialogHeader className="items-center text-center">
          <div className="mx-auto mb-4 flex size-12 items-center justify-center rounded-full">
            <motion.div
              initial={{ scale: 0 }}
              animate={isAnimating ? { scale: [0, 1.2, 1] } : { scale: 1 }}
              transition={{ duration: 2, ease: "easeOut" }}
            >
              <CheckCircle2 className="size-16 text-green-500" />
            </motion.div>
          </div>
          <AlertDialogTitle className="text-2xl">Thank You!</AlertDialogTitle>
          <AlertDialogDescription className="text-base">
            Your contact request has been submitted successfully. We"ll respond within 24 hours.
          </AlertDialogDescription>
        </AlertDialogHeader>

        <Separator className="my-1" />

        <div className="mt-2 pt-2">
          <h3 className="mb-3 text-center text-sm font-medium">Need immediate assistance?</h3>
          <div className="flex flex-col gap-2 text-sm text-muted-foreground">
            <div className="flex items-center justify-center">
              <svg className="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
              <span>+1 (555) 123-4567</span>
            </div>
            <div className="flex items-center justify-center">
              <svg className="mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              <span>support@luixtech.cn</span>
            </div>
          </div>
        </div>

        <AlertDialogFooter className="mt-6 !flex-col gap-3">
          <AlertDialogAction asChild>
            <Button asChild className="h-11 w-full">
              <Link to="/">
                Back to Homepage
              </Link>
            </Button>
          </AlertDialogAction>
          <Button variant="outline" className="h-11 w-full" onClick={() => onOpenChange(false)}>
            Send Another Message
          </Button>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  )
}