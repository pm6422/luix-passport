import { IconCheck } from "@tabler/icons-react"
import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"

export default function FeaturesPage() {
  const features = [
    {
      title: "Secure Authentication",
      description: "Industry-standard encryption and security protocols to protect user credentials",
      items: [
        "OAuth 2.0 & OpenID Connect support",
        "Password hashing with bcrypt",
        "Multi-factor authentication",
        "JWT token-based sessions"
      ]
    },
    {
      title: "Easy Integration",
      description: "Simple implementation for any application stack",
      items: [
        "RESTful API endpoints",
        "React/Next.js components",
        "TypeScript support",
        "Webhook notifications"
      ]
    },
    {
      title: "User Management",
      description: "Complete control over user accounts and permissions",
      items: [
        "Profile management",
        "Role-based access control",
        "Account verification flows",
        "Password reset functionality"
      ]
    },
    {
      title: "Analytics & Security",
      description: "Monitor and protect your user accounts",
      items: [
        "Login activity tracking",
        "Suspicious activity detection",
        "Device fingerprinting",
        "Automated security audits"
      ]
    }
  ];

  return (
    <div className="container relative grid h-screen flex-col items-center justify-center lg:max-w-none lg:grid-cols-3">
      {/* Left side - hidden on mobile - now takes 1/3 */}
      <div className="lg:flex relative hidden lg:col-span-1 lg:px-12 ms-10 mb-36">
        <div className="max-md:text-center">
          <img
            src="/assets/images/logos/logo-with-text.svg"
            alt="Logo"
            className="h-14 my-12"
          />
          <h2 className="lg:text-5xl text-4xl font-extrabold lg:leading-[55px]">
            Powerful Authentication Features
          </h2>
          <h4 className="mt-6 text-lg">
            Our Passport system provides everything you need for secure, scalable user authentication
          </h4>
          <div className="mt-10">
            <Button asChild className="px-8 py-4 h-14 text-base rounded-2xl">
              <Link to="/sign-up">Get Started</Link>
            </Button>
          </div>
        </div>
      </div>

      {/* Right side - content - now takes 2/3 */}
      <div className="flex flex-col w-full h-full lg:col-span-2 xl:px-32 lg:px-20 px-5 rounded-3xl py-10 overflow-y-auto">
        {/*<SiteHeader />*/}

        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2">Passport Login System Features</h1>
          <p className="text-lg text-muted-foreground mb-10">
            Everything you need for modern user authentication
          </p>

          <div className="grid md:grid-cols-2 gap-8">
            {features.map((feature, index) => (
              <div key={index} className="border rounded-2xl p-6 hover:shadow-md transition-shadow">
                <h3 className="text-xl font-semibold mb-3">{feature.title}</h3>
                <p className="text-muted-foreground mb-4">{feature.description}</p>
                <ul className="space-y-2">
                  {feature.items.map((item, itemIndex) => (
                    <li key={itemIndex} className="flex items-start">
                      <IconCheck className="h-5 w-5 text-green-500 mr-2 mt-0.5 flex-shrink-0" />
                      <span>{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>

          <div className="mt-16 text-center">
            <h3 className="text-2xl font-semibold mb-4">Ready to implement Passport?</h3>
            <div className="flex justify-center space-x-4">
              <Button asChild className="px-8 py-4 h-14 text-base rounded-2xl">
                <Link to="/sign-up">Start Free Trial</Link>
              </Button>
              <Button asChild variant="outline" className="px-8 py-4 h-14 text-base rounded-2xl">
                <Link to="/contact-us">Contact Sales</Link>
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}