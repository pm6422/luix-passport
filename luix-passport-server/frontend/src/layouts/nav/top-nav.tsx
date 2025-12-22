import { cn } from "@/lib/utils"
import { Link, useLocation } from "react-router-dom"
import { Fragment } from "react"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Button } from "@/components/ui/button"
import { IconMenu } from "@tabler/icons-react"

interface Props extends React.HTMLAttributes<HTMLElement> {
  links: {
    title: string
    href: string
  }[]
}

export function TopNav({ className, links, ...props }: Props) {
  const location = useLocation()

  return (
    <>
      <div className="md:hidden">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button size="icon" variant="outline">
              <IconMenu />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent side="bottom" align="start">
            {links.map(({ title, href }) => {
              const isActive = location.pathname === href
              return (
                <DropdownMenuItem key={`${title}-${href}`} asChild>
                  <Link
                    to={href}
                    className={!isActive ? "text-muted-foreground" : ""}
                  >
                    {title}
                  </Link>
                </DropdownMenuItem>
              )
            })}
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <nav
        className={cn(
          "hidden items-center md:flex",
          className
        )}
        {...props}
      >
        {links.map(({ title, href }, index) => {
          const isActive = location.pathname === href
          return (
            <Fragment key={`${title}-${href}`}>
              {index > 0 && <span className="mx-4 text-border select-none">|</span>}
              <Link
                to={href}
                className={`text-sm font-medium transition-colors hover:text-primary ${isActive ? "" : "text-muted-foreground"
                  }`}
              >
                {title}
              </Link>
            </Fragment>
          )
        })}
      </nav>
    </>
  )
}
