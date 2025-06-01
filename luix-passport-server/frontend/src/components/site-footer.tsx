import { Link } from "react-router-dom"

export function SiteFooter() {
  return (
    <footer className="mt-auto pt-2">
      <div className="flex items-center justify-between">
        <p className="text-xs text-muted-foreground">
          © 2025 LUIX Universe. All rights reserved.
        </p>
        <div className="flex space-x-6">
          <Link
            to="/terms-of-service"
            className="text-xs text-muted-foreground hover:text-primary"
          >
            Terms
          </Link>
          <Link
            to="/privacy-policy"
            className="text-xs text-muted-foreground hover:text-primary"
          >
            Privacy
          </Link>
        </div>
      </div>
    </footer>
  )
}