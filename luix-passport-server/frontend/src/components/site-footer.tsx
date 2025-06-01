import { Link } from "react-router-dom"

export function SiteFooter() {
  return (
    <footer className="mt-auto pt-2">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between">
        {/* 链接部分 - 小屏隐藏 */}
        <div className="hidden sm:flex space-x-6">
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

        {/* 版权信息 - 始终居中，小屏独占一行 */}
        <p className="text-xs text-muted-foreground text-center sm:text-left">
          © 2025 LUIX Universe
        </p>

        {/* 小屏占位的空div保持平衡 */}
        <div className="sm:hidden flex-1"></div>
      </div>
    </footer>
  )
}